package parser.nodes.expressions;

import parser.nodes.ASTVisitor;

public class UnaryOperatorNode implements ExpressionNode {
    public ExpressionNode operand;
    public String operator;

    public UnaryOperatorNode(ExpressionNode operand, String operator) {
        this.operand = operand;
        this.operator = operator;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ExpressionNode.super.accept(visitor, data);

        operand.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "UnaryOperatorNode{" +
                "operand=" + operand +
                ", operator='" + operator + '\'' +
                '}';
    }
}
