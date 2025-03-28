package generators.ast.statements;

import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.statements.SwitchStatementNode;
import parser.nodes.statements.CaseNode;

import java.util.List;

public class SwitchStatementNodeGenerator {
    private ExpressionBaseNode condition;
    private List<CaseNode> cases;
    private BlockNode defaultBlock;

    public static SwitchStatementNodeGenerator builder() {
        return new SwitchStatementNodeGenerator();
    }

    public SwitchStatementNodeGenerator condition(ExpressionBaseNode condition) {
        this.condition = condition;
        return this;
    }

    public SwitchStatementNodeGenerator cases(List<CaseNode> cases) {
        this.cases = cases;
        return this;
    }

    public SwitchStatementNodeGenerator defaultBlock(BlockNode defaultBlock) {
        this.defaultBlock = defaultBlock;
        return this;
    }

    public SwitchStatementNode build() {
        return new SwitchStatementNode(condition, cases, new BodyNode(defaultBlock));
    }
}
