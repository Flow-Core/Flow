package semantic_analysis.loaders;

import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.scopes.Scope;
import semantic_analysis.visitors.ExpressionTraverse;
import semantic_analysis.visitors.ExpressionTraverse.TypeWrapper;

import java.util.Objects;

public class VariableLoader {
    public static void loadDeclaration(
        final FieldNode fieldNode,
        final Scope scope
    ) {
        if (fieldNode.initialization == null) {
            throw new SA_SemanticError("Variable must either have an explicit type or be initialized");
        }

        final boolean isConst = fieldNode.initialization.declaration.modifier.equals("const");
        if (scope.type() == Scope.Type.FUNCTION && isConst) {
            throw new SA_SemanticError("Local variable cannot be const");
        }

        final TypeWrapper varType = new TypeWrapper(
            fieldNode.initialization.declaration.type,
            false,
            fieldNode.initialization.declaration.isNullable
        );
        TypeWrapper actualType = null;
        if (fieldNode.initialization.assignment != null) {
            actualType = new ExpressionTraverse().traverse(fieldNode.initialization.assignment.value, scope, isConst);
            if (isConst && !(fieldNode.initialization.assignment.value.expression instanceof LiteralNode)) {
                throw new SA_SemanticError("Const must contain a literal");
            }

            fieldNode.isInitialized = true;
        } else if (varType.type() == null) {
            throw new SA_SemanticError("Variable must either have an explicit type or be initialized");
        } else if (isConst) {
            throw new SA_SemanticError("Const must be initialized");
        }

        if (varType.type() != null) {
            if (!scope.findTypeDeclaration(varType.type())) {
                throw new SA_UnresolvedSymbolException(varType.type());
            }

            if (actualType == null || actualType.type() == null) {
                scope.symbols().fields().add(fieldNode);
                return;
            }

            if (actualType.type().equals("null")) {
                if (!fieldNode.initialization.declaration.isNullable) {
                    throw new SA_SemanticError("Null cannot be a value of a non-null type '" + fieldNode.initialization.declaration.type + "'");
                }
            } else if (!scope.isSameType(actualType, varType)) {
                throw new SA_SemanticError("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'");
            }
        } else {
            if (actualType.type().equals("null")) {
                throw new SA_SemanticError("Cannot infer variable type from 'null'");
            }
            fieldNode.initialization.declaration.type = actualType.type();
            fieldNode.initialization.declaration.isNullable = actualType.isNullable();
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

        final TypeWrapper varType = new TypeWrapper(
            fieldNode.initialization.declaration.type,
            false,
            fieldNode.initialization.declaration.isNullable
        );
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
        TypeWrapper actualType = new ExpressionTraverse().traverse(expressionBase, scope);
        fieldNode.isInitialized = true;

        if (actualType.type().equals("null")) {
            if (!fieldNode.initialization.declaration.isNullable) {
                throw new SA_SemanticError("Null cannot be a value of a non-null type '" + fieldNode.initialization.declaration.type + "'");
            }
        } else if (!scope.isSameType(actualType, varType)) {
            throw new SA_SemanticError("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'");
        }
    }

    private static FieldNode getFieldNode(VariableAssignmentNode variableAssignment, Scope scope) {
        final FieldNode fieldNode = scope.getField(((VariableReferenceNode) variableAssignment.variable.expression).variable);
        if (fieldNode == null) {
            throw new SA_UnresolvedSymbolException(((VariableReferenceNode) variableAssignment.variable.expression).variable);
        }

        if (
            fieldNode.initialization.declaration.modifier.equals("const")
                || fieldNode.initialization.declaration.modifier.equals("val") && fieldNode.isInitialized
        ) {
            throw new SA_SemanticError(fieldNode.initialization.declaration.modifier + " cannot be reassigned");
        }

        if (scope.type() == Scope.Type.FUNCTION && !fieldNode.modifiers.isEmpty()) {
            throw new SA_SemanticError("Modifier are not applicable to 'local variable'");
        }

        return fieldNode;
    }
}