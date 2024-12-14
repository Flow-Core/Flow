package parser.nodes;

public class BinaryExpressionNode implements ExpressionNode {
    private final ASTNode left;
    private final ASTNode right;
    private final String operator;

    public BinaryExpressionNode(final ASTNode left, final ASTNode right, final String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }
}
