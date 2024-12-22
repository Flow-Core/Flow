package parser.nodes.statements;

import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;

public class WhileStatementNode {
    private final ExpressionNode condition;
    private final BlockNode loopBlock;

    public WhileStatementNode(final ExpressionNode condition,
                              final BlockNode loopBlock) {
        this.condition = condition;
        this.loopBlock = loopBlock;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockNode getLoopBlock() {
        return loopBlock;
    }
}
