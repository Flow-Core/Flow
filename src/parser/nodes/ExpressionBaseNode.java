package parser.nodes;

public class ExpressionBaseNode {
    public ExpressionNode expression;

    public ExpressionBaseNode(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return expression.toString();
    }
}
