package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;

import java.util.Objects;

public class InitializedVariableNode implements ASTNode {
    public VariableDeclarationNode declaration;
    public VariableAssignmentNode assignment;

    public InitializedVariableNode(VariableDeclarationNode declaration, VariableAssignmentNode assignment) {
        this.declaration = declaration;
        this.assignment = assignment;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        if (declaration != null) {
            declaration.accept(visitor, data);
        }

        if (assignment != null) {
            assignment.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InitializedVariableNode that = (InitializedVariableNode) o;

        if (!Objects.equals(declaration, that.declaration)) return false;
        return Objects.equals(assignment, that.assignment);
    }

    @Override
    public int hashCode() {
        int result = declaration != null ? declaration.hashCode() : 0;
        result = 31 * result + (assignment != null ? assignment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (assignment != null) {
            return declaration + " = " + assignment.value;
        } else {
            return declaration.toString();
        }
    }
}
