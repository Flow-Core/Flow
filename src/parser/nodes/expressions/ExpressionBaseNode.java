package parser.nodes.expressions;

import parser.nodes.ASTMetaDataStore;
import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;

import java.util.Objects;

public class ExpressionBaseNode implements ASTNode {
    public ExpressionNode expression;

    public ExpressionBaseNode(ExpressionNode expression, int line, String file) {
        this.expression = expression;

        ASTMetaDataStore.getInstance().addMetadata(this, line, file);
    }

    public ExpressionBaseNode(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        if (expression != null) {
            expression.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionBaseNode that = (ExpressionBaseNode) o;

        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return expression != null ? expression.hashCode() : 0;
    }

    @Override
    public String toString() {
        return expression == null ? "'null'" : expression.toString();
    }
}
