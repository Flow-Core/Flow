package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;

public class CatchNode implements StatementNode {
    public ParameterNode parameter;
    public BlockNode body;

    public CatchNode(ParameterNode parameter, BlockNode body) {
        this.parameter = parameter;
        this.body = body;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        parameter.accept(visitor, data);

        body.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "CatchNode{" +
            "argument=" + parameter +
            ", body=" + body +
            '}';
    }
}
