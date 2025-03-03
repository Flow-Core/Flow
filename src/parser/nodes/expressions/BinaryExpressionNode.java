package parser.nodes.expressions;

import parser.nodes.ASTVisitor;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryExpressionNode that = (BinaryExpressionNode) o;

        if (!Objects.equals(left, that.left)) return false;
        if (!Objects.equals(right, that.right)) return false;
        return Objects.equals(operator, that.operator);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (operator.equals("[]")) return left + "[" + right + "]";
        return left + operator + right;
    }
}
