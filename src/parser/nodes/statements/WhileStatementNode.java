package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;

public class WhileStatementNode implements ASTNode {
    public ExpressionNode condition;
    public BlockNode loopBlock;

    public WhileStatementNode(ExpressionNode condition, BlockNode loopBlock) {
        this.condition = condition;
        this.loopBlock = loopBlock;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

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
