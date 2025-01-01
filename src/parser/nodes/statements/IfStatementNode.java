package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;

public class IfStatementNode implements ASTNode {
    private final ExpressionNode condition;
    private final BlockNode trueBranch;
    private final BlockNode falseBranch;

    public IfStatementNode(final ExpressionNode condition,
                           final BlockNode trueBranch,
                           final BlockNode falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public IfStatementNode(final ExpressionNode condition,
                           final BlockNode trueBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = null;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockNode getTrueBranch() {
        return trueBranch;
    }

    public BlockNode getFalseBranch() {
        return falseBranch;
    }
}
