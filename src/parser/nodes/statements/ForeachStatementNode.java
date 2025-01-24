package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;

public class ForeachStatementNode implements ASTNode {
    public String loopVariable;
    public ExpressionNode collection;
    public BlockNode body;

    public ForeachStatementNode(String loopVariable, ExpressionNode collection, BlockNode body) {
        this.loopVariable = loopVariable;
        this.collection = collection;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        collection.accept(visitor);

        body.accept(visitor);
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