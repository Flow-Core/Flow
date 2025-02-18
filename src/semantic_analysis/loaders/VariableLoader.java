package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.FlowType;
import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.visitors.ExpressionTraverse;

import java.util.Objects;

public class VariableLoader {
    public static void loadDeclaration(
        final FieldNode fieldNode,
        final Scope scope
    ) {
        if (fieldNode.initialization == null) {
            LoggerFacade.error("Variable must either have an explicit type or be initialized", fieldNode);
            return;
        }

        final boolean isConst = fieldNode.initialization.declaration.modifier.equals("const");
        if (scope.type() == Scope.Type.FUNCTION) {
            if (isConst) {
                LoggerFacade.error("Local variable cannot be const", fieldNode);
            }
        } else {
            ModifierLoader.load(
                fieldNode.modifiers,
                scope.type() == Scope.Type.TOP
                    ? ModifierLoader.ModifierType.TOP_LEVEL_FIELD
                    : ModifierLoader.ModifierType.CLASS_FIELD
            );

            if (ModifierLoader.isDefaultPublic(fieldNode.modifiers)) {
                fieldNode.modifiers.add("public");
            }
            if (fieldNode.initialization.declaration.modifier.equals("const")) {
                fieldNode.modifiers.add("final");
            }
        }

        final FlowType varType = fieldNode.initialization.declaration.type;
        FlowType actualType = null;
        if (fieldNode.initialization.assignment != null) {
            actualType = new ExpressionTraverse().traverse(fieldNode.initialization.assignment.value, scope, isConst);
            if (isConst && !(fieldNode.initialization.assignment.value.expression instanceof LiteralNode)) {
                LoggerFacade.error("Const must contain a literal", fieldNode);
            }

            fieldNode.isInitialized = true;
        } else if (varType == null || varType.name == null) {
            LoggerFacade.error("Variable must either have an explicit type or be initialized", fieldNode);
            return;
        } else if (isConst) {
            LoggerFacade.error("Const must be initialized", fieldNode);
        }

        if (varType != null) {
            if (varType.name == null || !scope.findTypeDeclaration(varType.name)) {
                LoggerFacade.error("Unresolved symbol: '" + varType + "'", fieldNode);
                return;
            }

            if (actualType == null || actualType.name == null) {
                scope.symbols().fields().add(fieldNode);
                return;
            }

            if (actualType.name.equals("null")) {
                if (!fieldNode.initialization.declaration.type.isNullable) {
                    LoggerFacade.error("Null cannot be a value of a non-null type '" + fieldNode.initialization.declaration.type + "'", fieldNode);
                }
            } else if (!scope.isSameType(actualType, varType)) {
                LoggerFacade.error("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'", fieldNode);
            }
        } else {
            if (actualType != null)
                if (actualType.name.equals("null")) {
                    LoggerFacade.error("Cannot infer variable type from 'null'", fieldNode);
                } else {
                    fieldNode.initialization.declaration.type = actualType;
                }
        }

        if (scope.type() == Scope.Type.TOP && !fieldNode.modifiers.contains("static")) {
            fieldNode.modifiers.add("static");
        }

        scope.symbols().fields().add(fieldNode);
    }

    public static void loadAssignment(
        final VariableAssignmentNode variableAssignment,
        final Scope scope
    ) {
        final FieldNode fieldNode = getFieldNode(variableAssignment, scope);
        if (fieldNode == null) {
            return;
        }

        final FlowType varType = fieldNode.initialization.declaration.type;
        ExpressionBaseNode expressionBase = variableAssignment.value;
        if (!Objects.equals(variableAssignment.operator, "=")) {
            final String[] operators = variableAssignment.operator.split("");
            expressionBase = new ExpressionBaseNode(
                new BinaryExpressionNode(
                    variableAssignment.variable.expression,
                    variableAssignment.value.expression,
                    operators[0]
                )
            );
            variableAssignment.operator = "=";
        }
        FlowType actualType = new ExpressionTraverse().traverse(expressionBase, scope);
        fieldNode.isInitialized = true;

        if (actualType == null || actualType.name.equals("null")) {
            if (!fieldNode.initialization.declaration.type.isNullable) {
                LoggerFacade.error("Null cannot be a value of a non-null type '" + fieldNode.initialization.declaration.type + "'", fieldNode);
            }
        } else if (!scope.isSameType(actualType, varType)) {
            LoggerFacade.error("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'", fieldNode);
        }
    }

    private static FieldNode getFieldNode(VariableAssignmentNode variableAssignment, Scope scope) {
        final FieldNode fieldNode = scope.getField(((VariableReferenceNode) variableAssignment.variable.expression).variable);
        if (fieldNode == null) {
            LoggerFacade.error("Unresolved symbol: '" + ((VariableReferenceNode) variableAssignment.variable.expression).variable + "'", variableAssignment);
            return null;
        }

        if (
            fieldNode.initialization.declaration.modifier.equals("const")
                || fieldNode.initialization.declaration.modifier.equals("val") && fieldNode.isInitialized
        ) {
            LoggerFacade.error(fieldNode.initialization.declaration.modifier + " cannot be reassigned", variableAssignment);
        }

        if (scope.type() == Scope.Type.FUNCTION && !fieldNode.modifiers.isEmpty()) {
            LoggerFacade.error("Modifier are not applicable to 'local variable'", variableAssignment);
        }

        return fieldNode;
    }
}