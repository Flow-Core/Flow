package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionNode;

public class ForeachStatementNode implements StatementNode {
    public String loopVariable;
    public ExpressionNode collection;
    public BlockNode body;

    public ForeachStatementNode(String loopVariable, ExpressionNode collection, BlockNode body) {
        this.loopVariable = loopVariable;
        this.collection = collection;
        this.body = body;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        collection.accept(visitor, data);

        body.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "ForeachStatementNode{" +
            "loopVariable=" + loopVariable +
            ", collection=" + collection +
            ", body=" + body +
            '}';
    }
}