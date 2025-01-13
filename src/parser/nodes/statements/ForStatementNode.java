package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.InitializedVariable;
import parser.nodes.variable.VariableAssignmentNode;

public class ForStatementNode implements ASTNode {
    public InitializedVariable initialization;
    public ExpressionNode condition;
    public VariableAssignmentNode action;
    public BlockNode loopBlock;

    public ForStatementNode(InitializedVariable initialization, ExpressionNode condition, VariableAssignmentNode action, BlockNode loopBlock) {
        this.initialization = initialization;
        this.condition = condition;
        this.action = action;
        this.loopBlock = loopBlock;
    }
}
