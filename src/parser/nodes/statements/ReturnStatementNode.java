package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class ReturnStatementNode implements StatementNode {
    public ExpressionBaseNode returnValue;

    public ReturnStatementNode(ExpressionBaseNode throwValue) {
        this.returnValue = throwValue;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        returnValue.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnStatementNode that = (ReturnStatementNode) o;

        return Objects.equals(returnValue, that.returnValue);
    }

    @Override
    public int hashCode() {
        return returnValue != null ? returnValue.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ReturnStatementNode{" +
            "returnValue=" + returnValue +
            '}';
    }
}
