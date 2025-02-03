package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.VariableAssignmentNode;

public class ForStatementNode implements StatementNode {
    public VariableAssignmentNode initialization;
    public ExpressionNode condition;
    public VariableAssignmentNode action;
    public BlockNode loopBlock;

    public ForStatementNode(VariableAssignmentNode initialization, ExpressionNode condition, VariableAssignmentNode action, BlockNode loopBlock) {
        this.initialization = initialization;
        this.condition = condition;
        this.action = action;
        this.loopBlock = loopBlock;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        condition.accept(visitor, data);

        action.accept(visitor, data);

        loopBlock.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "ForStatementNode{" +
            "initialization=" + initialization +
            ", condition=" + condition +
            ", action=" + action +
            ", loopBlock=" + loopBlock +
            '}';
    }
}
