package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;

import java.util.Objects;

public class CatchNode implements StatementNode {
    public ParameterNode parameter;
    public BodyNode body;

    public CatchNode(ParameterNode parameter, BodyNode body) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatchNode catchNode = (CatchNode) o;

        if (!Objects.equals(parameter, catchNode.parameter)) return false;
        return Objects.equals(body, catchNode.body);
    }

    @Override
    public int hashCode() {
        int result = parameter != null ? parameter.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CatchNode{" +
            "argument=" + parameter +
            ", body=" + body +
            '}';
    }
}
