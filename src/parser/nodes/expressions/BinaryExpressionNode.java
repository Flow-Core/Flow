package parser.nodes.expressions;

import parser.nodes.ASTVisitor;

public class BinaryExpressionNode implements ExpressionNode {
    public ExpressionNode left;
    public ExpressionNode right;
    public String operator;

    public BinaryExpressionNode(ExpressionNode left, ExpressionNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ExpressionNode.super.accept(visitor);

        left.accept(visitor);
        right.accept(visitor);
    }

    @Override
    public String toString() {
        return "BinaryExpressionNode{" +
            "left=" + left +
            ", right=" + right +
            ", operator='" + operator + '\'' +
            '}';
    }
}
