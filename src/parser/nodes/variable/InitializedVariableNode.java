package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;

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
    public String toString() {
        return "InitializedVariableNode{" +
            "declaration=" + declaration +
            ", assignment=" + assignment +
            '}';
    }
}
