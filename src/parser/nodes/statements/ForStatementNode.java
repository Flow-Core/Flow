package parser.nodes.statements;

import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.VariableAssignment;
import parser.nodes.variable.VariableDeclaration;

public class ForStatementNode {
    private final VariableDeclaration initialization;
    private final ExpressionNode condition;
    private final VariableAssignment action;
    private final BlockNode loopBlock;

    public ForStatementNode(final VariableDeclaration initialization,
                            final ExpressionNode condition,
                            final VariableAssignment action,
                            final BlockNode loopBlock) {
        this.initialization = initialization;
        this.condition = condition;
        this.action = action;
        this.loopBlock = loopBlock;
    }

    public VariableDeclaration getInitialization() {
        return initialization;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public VariableAssignment getAction() {
        return action;
    }

    public BlockNode getLoopBlock() {
        return loopBlock;
    }
}
