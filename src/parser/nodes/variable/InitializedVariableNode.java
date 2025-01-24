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
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        if (declaration != null) {
            declaration.accept(visitor);
        }

        if (assignment != null) {
            assignment.accept(visitor);
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
