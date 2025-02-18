package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class IfStatementNode implements StatementNode {
    public ExpressionBaseNode condition;
    public BodyNode trueBranch;
    public BodyNode falseBranch;

    public IfStatementNode(ExpressionBaseNode condition, BodyNode trueBranch, BodyNode falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        condition.accept(visitor, data);

        trueBranch.accept(visitor, data);

        if (falseBranch != null) {
            falseBranch.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IfStatementNode that = (IfStatementNode) o;

        if (!Objects.equals(condition, that.condition)) return false;
        if (!Objects.equals(trueBranch, that.trueBranch)) return false;
        return Objects.equals(falseBranch, that.falseBranch);
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + (trueBranch != null ? trueBranch.hashCode() : 0);
        result = 31 * result + (falseBranch != null ? falseBranch.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IfStatementNode{" +
            "condition=" + condition +
            ", trueBranch=" + trueBranch +
            ", falseBranch=" + falseBranch +
            '}';
    }
}
