package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class ReturnStatementNode implements StatementNode {
    public ExpressionBaseNode returnValue;
    public FlowType returnType;

    public ReturnStatementNode(ExpressionBaseNode returnValue, FlowType returnType) {
        this.returnValue = returnValue;
        this.returnType = returnType;
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

        if (!Objects.equals(returnValue, that.returnValue)) return false;
        return Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        int result = returnValue != null ? returnValue.hashCode() : 0;
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReturnStatementNode{" +
            "returnValue=" + returnValue +
            ", returnType=" + returnType +
            '}';
    }
}
