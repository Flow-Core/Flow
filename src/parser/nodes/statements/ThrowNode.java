package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

public class ThrowNode implements StatementNode {
    public ExpressionBaseNode throwValue;

    public ThrowNode(ExpressionBaseNode throwValue) {
        this.throwValue = throwValue;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        throwValue.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "ThrowNode{" +
            "throwValue=" + throwValue +
            '}';
    }
}
