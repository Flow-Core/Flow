package parser.nodes.expressions;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;

public class ExpressionBaseNode implements ASTNode {
    public ExpressionNode expression;

    public ExpressionBaseNode(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        expression.accept(visitor);
    }

    @Override
    public String toString() {
        return expression.toString();
    }
}
