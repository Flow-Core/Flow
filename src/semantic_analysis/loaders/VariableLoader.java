package semantic_analysis.loaders;

import parser.nodes.classes.FieldNode;
import parser.nodes.variable.VariableAssignmentNode;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.scopes.Scope;
import semantic_analysis.visitors.ExpressionTraverse;

import java.util.Objects;

public class VariableLoader {
    public void loadDeclaration(
        final FieldNode fieldNode,
        final Scope parent
    ) {
        final String varType = fieldNode.initialization.declaration.type;
        String actualType = null;
        if (fieldNode.initialization.assignment.value != null) {
            actualType = new ExpressionTraverse().traverse(fieldNode.initialization.assignment.value, parent);
            fieldNode.isInitialized = true;
        } else if (varType == null) {
            throw new SA_SemanticError("Variable must either have an explicit type or be initialized");
        }

        if (varType != null) {
            if (!Objects.equals(actualType, varType) && !parent.isSameType(actualType, varType)) {
                throw new SA_SemanticError("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'");
            }
        } else {
            fieldNode.initialization.declaration.type = actualType;
        }
    }

    public void loadAssignment(
        final VariableAssignmentNode variableAssignment,
        final Scope parent
    ) {
        final FieldNode fieldNode = parent.getField(variableAssignment.variable);
        if (fieldNode == null) {
            throw new SA_UnresolvedSymbolException(variableAssignment.variable);
        }

        if (!fieldNode.initialization.declaration.modifier.equals("var")) {
            throw new SA_SemanticError(fieldNode.initialization.declaration.modifier + " cannot be reassigned");
        }

        final String varType = fieldNode.initialization.declaration.type;
        String actualType = new ExpressionTraverse().traverse(variableAssignment.value, parent);
        fieldNode.isInitialized = true;

        if (!Objects.equals(actualType, varType) && !parent.isSameType(actualType, varType)) {
            throw new SA_SemanticError("Type mismatch: expected '"  + varType + "' but received '" + actualType + "'");
        }
    }
}
