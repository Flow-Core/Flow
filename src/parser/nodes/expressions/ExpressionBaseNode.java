package parser.nodes.expressions;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;

public class ExpressionBaseNode implements ASTNode {
    public ExpressionNode expression;

    public ExpressionBaseNode(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        expression.accept(visitor, data);
    }

    @Override
    public String toString() {
        return expression == null ? "'null'" : expression.toString();
    }
}
