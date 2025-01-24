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
    public void accept(ASTVisitor visitor) {
        ExpressionNode.super.accept(visitor);

        operand.accept(visitor);
    }

    @Override
    public String toString() {
        return "UnaryOperatorNode{" +
                "operand=" + operand +
                ", operator='" + operator + '\'' +
                '}';
    }
}
