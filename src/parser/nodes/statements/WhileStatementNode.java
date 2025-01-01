package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;

public class WhileStatementNode implements ASTNode {
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
