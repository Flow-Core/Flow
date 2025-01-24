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
    public <D> void accept(final ASTVisitor<D> visitor, D data) {
        ExpressionNode.super.accept(visitor, data);

        left.accept(visitor, data);
        right.accept(visitor, data);
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
