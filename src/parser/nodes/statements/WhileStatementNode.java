package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class WhileStatementNode implements StatementNode {
    public ExpressionBaseNode condition;
    public BlockNode loopBlock;

    public WhileStatementNode(ExpressionBaseNode condition, BlockNode loopBlock) {
        this.condition = condition;
        this.loopBlock = loopBlock;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        condition.accept(visitor, data);

        loopBlock.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhileStatementNode that = (WhileStatementNode) o;

        if (!Objects.equals(condition, that.condition)) return false;
        return Objects.equals(loopBlock, that.loopBlock);
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + (loopBlock != null ? loopBlock.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WhileStatementNode{" +
            "condition=" + condition +
            ", loopBlock=" + loopBlock +
            '}';
    }
}
