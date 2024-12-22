package parser.nodes.variable;

import parser.nodes.ExpressionNode;

public class VariableReference implements ExpressionNode {
    private final String variable;

    public VariableReference(final String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return "Variable reference :" + variable;
    }
}
