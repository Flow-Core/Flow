package generators.ast.statements;

import parser.nodes.statements.IfStatementNode;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.ArrayList;

public class IfStatementNodeGenerator {
    private ExpressionBaseNode condition;
    private BlockNode trueBranch = new BlockNode(new ArrayList<>());
    private BlockNode falseBranch = null;

    public static IfStatementNodeGenerator builder() {
        return new IfStatementNodeGenerator();
    }

    public IfStatementNodeGenerator condition(ExpressionBaseNode condition) {
        this.condition = condition;
        return this;
    }

    public IfStatementNodeGenerator trueBranch(BlockNode trueBranch) {
        this.trueBranch = trueBranch;
        return this;
    }

    public IfStatementNodeGenerator falseBranch(BlockNode falseBranch) {
        this.falseBranch = falseBranch;
        return this;
    }

    public IfStatementNode build() {
        return new IfStatementNode(condition, trueBranch, falseBranch);
    }
}