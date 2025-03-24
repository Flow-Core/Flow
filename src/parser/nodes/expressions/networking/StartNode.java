package parser.nodes.expressions.networking;

import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class StartNode implements ExpressionNode {
    public FlowType serverType;
    public ExpressionBaseNode port;

    public StartNode(FlowType serverType, ExpressionBaseNode port) {
        this.serverType = serverType;
        this.port = port;
    }

    @Override
    public <D> void accept(ASTVisitor<D> visitor, D data) {
        ExpressionNode.super.accept(visitor, data);

        port.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StartNode that)) return false;
        return Objects.equals(serverType, that.serverType) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverType, port);
    }

    @Override
    public String toString() {
        return serverType + " ~ " + port;
    }
}
