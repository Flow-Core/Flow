package semantic_analysis.loaders;

import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.scopes.Scope;
import semantic_analysis.visitors.ExpressionTraverse;

import java.util.Objects;

public class VariableLoader {
    public void loadDeclaration(
        final FieldNode fieldNode,
        final Scope scope
    ) {
        final String varType = fieldNode.initialization.declaration.type;
        String actualType = null;
        if (fieldNode.initialization.assignment.value != null) {
            actualType = new ExpressionTraverse().traverse(fieldNode.initialization.assignment.value, scope);
            fieldNode.isInitialized = true;
        } else if (varType == null) {
            throw new SA_SemanticError("Variable must either have an explicit type or be initialized");
        }

        if (actualType == null) {
            return;
        }

        if (varType != null) {
            if (!scope.findTypeDeclaration(varType)) {
                throw new SA_UnresolvedSymbolException(varType);
            }
            if (actualType.equals("null")) {
                if (!fieldNode.initialization.declaration.isNullable) {
                    throw new SA_SemanticError("Null cannot be a value of a non-null type");
                }
            } else if (!Objects.equals(actualType, varType) && !scope.isSameType(actualType, varType)) {
                throw new SA_SemanticError("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'");
            }
        } else {
            if (actualType.equals("null")) {
                throw new SA_SemanticError("Variable must either have an explicit type or be initialized");
            }
            fieldNode.initialization.declaration.type = actualType;
        }

        scope.symbols().fields().add(fieldNode);
    }

    public void loadAssignment(
        final VariableAssignmentNode variableAssignment,
        final Scope scope
    ) {
        final FieldNode fieldNode = getFieldNode(variableAssignment, scope);

        final String varType = fieldNode.initialization.declaration.type;
        ExpressionBaseNode expressionBase = variableAssignment.value;
        if (!Objects.equals(variableAssignment.operator, "=")) {
            final String[] operators = variableAssignment.operator.split("");
            expressionBase = new ExpressionBaseNode(
                new BinaryExpressionNode(
                    new VariableReferenceNode(variableAssignment.variable),
                    variableAssignment.value.expression,
                    operators[0]
                )
            );
            variableAssignment.operator = "=";
        }
        String actualType = new ExpressionTraverse().traverse(expressionBase, scope);
        fieldNode.isInitialized = true;

        if (actualType.equals("null")) {
            if (!fieldNode.initialization.declaration.isNullable) {
                throw new SA_SemanticError("Null cannot be a value of a non-null type");
            }
        } else if (!Objects.equals(actualType, varType) && !scope.isSameType(actualType, varType)) {
            throw new SA_SemanticError("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'");
        }
    }

    private static FieldNode getFieldNode(VariableAssignmentNode variableAssignment, Scope scope) {
        final FieldNode fieldNode = scope.getField(variableAssignment.variable);
        if (fieldNode == null) {
            throw new SA_UnresolvedSymbolException(variableAssignment.variable);
        }

        if (!fieldNode.initialization.declaration.modifier.equals("var")) {
            throw new SA_SemanticError(fieldNode.initialization.declaration.modifier + " cannot be reassigned");
        }

        if (scope.type() == Scope.Type.FUNCTION && !fieldNode.modifiers.isEmpty()) {
            throw new SA_SemanticError("Modifier are not applicable to 'local variable'");
        }

        return fieldNode;
    }
}