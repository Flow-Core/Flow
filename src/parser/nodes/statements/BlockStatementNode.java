package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class BlockStatementNode implements StatementNode {
    public ExpressionBaseNode blockCondition;

    public BlockStatementNode(ExpressionBaseNode blockCondition) {
        this.blockCondition = blockCondition;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        if (blockCondition != null) {
            blockCondition.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockStatementNode other = (BlockStatementNode) o;

        return Objects.equals(blockCondition, other.blockCondition);
    }

    @Override
    public int hashCode() {
        return blockCondition != null ? blockCondition.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BlockStatementNode{" +
            "blockCondition=" + blockCondition +
            '}';
    }
}
