package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeachStatementNode that = (ForeachStatementNode) o;

        if (!Objects.equals(loopVariable, that.loopVariable)) return false;
        if (!Objects.equals(collection, that.collection)) return false;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = loopVariable != null ? loopVariable.hashCode() : 0;
        result = 31 * result + (collection != null ? collection.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
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