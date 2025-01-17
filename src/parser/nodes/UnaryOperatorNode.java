package parser.nodes;

public class UnaryOperatorNode implements ExpressionNode {
    public ExpressionNode operand;
    public String operator;

    public UnaryOperatorNode(ExpressionNode operand, String operator) {
        this.operand = operand;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "UnaryOperatorNode{" +
                "operand=" + operand +
                ", operator='" + operator + '\'' +
                '}';
    }
}
