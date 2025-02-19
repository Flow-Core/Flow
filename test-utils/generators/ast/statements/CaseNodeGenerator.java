package generators.ast.statements;

import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.statements.CaseNode;
import parser.nodes.expressions.ExpressionBaseNode;

public class CaseNodeGenerator {
    private ExpressionBaseNode value;
    private BlockNode body;

    public static CaseNodeGenerator builder() {
        return new CaseNodeGenerator();
    }

    public CaseNodeGenerator value(ExpressionBaseNode value) {
        this.value = value;
        return this;
    }

    public CaseNodeGenerator body(BlockNode body) {
        this.body = body;
        return this;
    }

    public CaseNode build() {
        return new CaseNode(value, new BodyNode(body));
    }
}
