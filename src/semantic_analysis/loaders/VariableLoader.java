package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.FlowType;
import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;
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

        if (scope.symbols().findField(fieldNode.initialization.declaration.name)) {
            LoggerFacade.error("Symbol '" + fieldNode.initialization.declaration.name + "' redefined", fieldNode);
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
            actualType = new ExpressionTraverse().traverse(fieldNode.initialization.assignment.value, scope);
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
            if (varType.name == null || !TypeRecognize.findTypeDeclaration(varType.name, scope)) {
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
            } else if (!TypeRecognize.isSameType(actualType, varType, scope)) {
                LoggerFacade.error("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'", fieldNode);
            }
        } else {
            if (actualType != null) {
                if (actualType.name.equals("null")) {
                    LoggerFacade.error("Cannot infer variable type from 'null'", fieldNode);
                } else {
                    fieldNode.initialization.declaration.type = actualType;
                }
            }
        }

        if (scope.type() == Scope.Type.TOP && !fieldNode.modifiers.contains("static")) {
            fieldNode.modifiers.add("static");
        }

        if (fieldNode.initialization.declaration.type != null && fieldNode.initialization.declaration.type.shouldBePrimitive) {
            fieldNode.initialization.declaration.type.isPrimitive = true;
            fieldNode.initialization.declaration.type.shouldBePrimitive = false;
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
        if (!Objects.equals(variableAssignment.operator, "=")) {
            final String[] operators = variableAssignment.operator.split("");
            variableAssignment.value = new ExpressionBaseNode(
                new BinaryExpressionNode(
                    variableAssignment.variable.expression,
                    variableAssignment.value.expression,
                    operators[0]
                )
            );
            variableAssignment.operator = "=";
        }
        FlowType actualType = new ExpressionTraverse().traverse(variableAssignment.value, scope);
        fieldNode.isInitialized = true;

        if (actualType == null || actualType.name.equals("null")) {
            if (fieldNode.initialization.declaration.type != null && !fieldNode.initialization.declaration.type.isNullable) {
                LoggerFacade.error("Null cannot be a value of a non-null type '" + fieldNode.initialization.declaration.type + "'", fieldNode);
            }
        } else if (!TypeRecognize.isSameType(actualType, varType, scope)) {
            LoggerFacade.error("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'", fieldNode);
        }
    }

    private static FieldNode getFieldNode(VariableAssignmentNode variableAssignment, Scope scope) {
        new ExpressionTraverse().traverse(variableAssignment.variable, scope);

        final FieldNode fieldNode;

        if (variableAssignment.variable.expression instanceof VariableReferenceNode variableReference) {
            fieldNode = TypeRecognize.getField(variableReference.variable, scope);

            if (fieldNode == null) {
                LoggerFacade.error("Unresolved symbol: '" + variableReference.variable + "'", variableAssignment);
                return null;
            }

            if (scope.type() == Scope.Type.FUNCTION && !fieldNode.modifiers.isEmpty()) {
                LoggerFacade.error("Modifiers are not applicable to 'local variable'", variableAssignment);
            }
        } else if (variableAssignment.variable.expression instanceof FieldReferenceNode fieldReference) {
            fieldNode = fieldReference.declaration;
        } else {
            LoggerFacade.error("'" + variableAssignment.variable.expression + "' is not assignable", variableAssignment);
            return null;
        }

        if (
            fieldNode.initialization.declaration.modifier.equals("const")
                || fieldNode.initialization.declaration.modifier.equals("val") && fieldNode.isInitialized
        ) {
            LoggerFacade.error(fieldNode.initialization.declaration.modifier + " cannot be reassigned", variableAssignment);
        }

        return fieldNode;
    }
}