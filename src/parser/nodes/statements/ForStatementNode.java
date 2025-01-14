package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableAssignmentNode;

public class ForStatementNode implements ASTNode {
    public InitializedVariableNode initialization;
    public ExpressionNode condition;
    public VariableAssignmentNode action;
    public BlockNode loopBlock;

    public ForStatementNode(InitializedVariableNode initialization, ExpressionNode condition, VariableAssignmentNode action, BlockNode loopBlock) {
        this.initialization = initialization;
        this.condition = condition;
        this.action = action;
        this.loopBlock = loopBlock;
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
