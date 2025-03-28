package generators.ast.statements;

import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.statements.WhileStatementNode;

public class WhileStatementNodeGenerator {
    private ExpressionBaseNode condition;
    private BlockNode loopBlock;

    public static WhileStatementNodeGenerator builder() {
        return new WhileStatementNodeGenerator();
    }

    public WhileStatementNodeGenerator condition(ExpressionBaseNode condition) {
        this.condition = condition;
        return this;
    }

    public WhileStatementNodeGenerator loopBlock(BlockNode loopBlock) {
        this.loopBlock = loopBlock;
        return this;
    }

    public WhileStatementNode build() {
        return new WhileStatementNode(condition, new BodyNode(loopBlock));
    }
}
