package parser.nodes.expressions;

import parser.nodes.ASTVisitor;

import java.util.Objects;

public class UnaryOperatorNode implements ExpressionNode {
    public ExpressionNode operand;
    public String operator;
    public boolean isPostfix;

    public UnaryOperatorNode(ExpressionNode operand, String operator, boolean isPostfix) {
        this.operand = operand;
        this.operator = operator;
        this.isPostfix = isPostfix;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ExpressionNode.super.accept(visitor, data);

        operand.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnaryOperatorNode that = (UnaryOperatorNode) o;

        if (!Objects.equals(operand, that.operand)) return false;
        return Objects.equals(operator, that.operator);
    }

    @Override
    public int hashCode() {
        int result = operand != null ? operand.hashCode() : 0;
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return isPostfix ? operand + operator : operator + operand;
    }
}
