package generators.ast.statements;

import parser.nodes.components.BlockNode;
import parser.nodes.statements.CatchNode;
import parser.nodes.components.ParameterNode;

public class CatchNodeGenerator {
    private ParameterNode parameter;
    private BlockNode body;

    public static CatchNodeGenerator builder() {
        return new CatchNodeGenerator();
    }

    public CatchNodeGenerator parameter(ParameterNode parameter) {
        this.parameter = parameter;
        return this;
    }

    public CatchNodeGenerator body(BlockNode body) {
        this.body = body;
        return this;
    }

    public CatchNode build() {
        return new CatchNode(parameter, body);
    }
}
