package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThrowNode throwNode = (ThrowNode) o;

        return Objects.equals(throwValue, throwNode.throwValue);
    }

    @Override
    public int hashCode() {
        return throwValue != null ? throwValue.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ThrowNode{" +
            "throwValue=" + throwValue +
            '}';
    }
}
