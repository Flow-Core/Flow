package generators.ast.statements;

import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.statements.ForStatementNode;

public class ForStatementNodeGenerator {
    private VariableAssignmentNode initialization;
    private ExpressionBaseNode condition;
    private BlockNode action;
    private BlockNode loopBlock;

    public static ForStatementNodeGenerator builder() {
        return new ForStatementNodeGenerator();
    }

    public ForStatementNodeGenerator initialization(VariableAssignmentNode initialization) {
        this.initialization = initialization;
        return this;
    }

    public ForStatementNodeGenerator condition(ExpressionBaseNode condition) {
        this.condition = condition;
        return this;
    }

    public ForStatementNodeGenerator action(BlockNode action) {
        this.action = action;
        return this;
    }

    public ForStatementNodeGenerator loopBlock(BlockNode loopBlock) {
        this.loopBlock = loopBlock;
        return this;
    }

    public ForStatementNode build() {
        return new ForStatementNode(initialization, condition, action, loopBlock);
    }
}
