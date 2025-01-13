package parser.nodes;

public class BinaryExpressionNode implements ExpressionNode {
    public ExpressionNode left;
    public ExpressionNode right;
    public String operator;

    public BinaryExpressionNode(ExpressionNode left, ExpressionNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}
