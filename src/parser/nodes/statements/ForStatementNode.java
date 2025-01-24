package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.VariableAssignmentNode;

public class ForStatementNode implements ASTNode {
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
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        condition.accept(visitor);

        action.accept(visitor);

        loopBlock.accept(visitor);
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
