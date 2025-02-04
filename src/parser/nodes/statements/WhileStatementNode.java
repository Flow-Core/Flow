package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionBaseNode;

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
    public String toString() {
        return "WhileStatementNode{" +
            "condition=" + condition +
            ", loopBlock=" + loopBlock +
            '}';
    }
}
