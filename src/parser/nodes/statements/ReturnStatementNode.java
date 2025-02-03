package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;

public class ReturnStatementNode implements StatementNode {
    public ExpressionNode returnValue;

    public ReturnStatementNode(ExpressionNode throwValue) {
        this.returnValue = throwValue;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        returnValue.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "ReturnStatementNode{" +
            "returnValue=" + returnValue +
            '}';
    }
}
