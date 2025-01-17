package parser.nodes.variable;

import parser.nodes.ExpressionNode;

public class VariableReferenceNode implements ExpressionNode {
    public String variable;

    public VariableReferenceNode(String variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return "VariableReferenceNode{" +
            "variable='" + variable + '\'' +
            '}';
    }
}
