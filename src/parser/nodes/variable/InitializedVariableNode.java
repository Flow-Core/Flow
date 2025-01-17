package parser.nodes.variable;

import parser.nodes.ASTNode;

public class InitializedVariableNode implements ASTNode {
    public VariableDeclarationNode declaration;
    public VariableAssignmentNode assignment;

    public InitializedVariableNode(VariableDeclarationNode declaration, VariableAssignmentNode assignment) {
        this.declaration = declaration;
        this.assignment = assignment;
    }

    @Override
    public String toString() {
        return "InitializedVariableNode{" +
            "declaration=" + declaration +
            ", assignment=" + assignment +
            '}';
    }
}
