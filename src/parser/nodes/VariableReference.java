package parser.nodes;

public class VariableReference implements ExpressionNode {
    private final String variable;

    public VariableReference(final String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }
}
