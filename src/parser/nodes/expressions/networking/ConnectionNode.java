package parser.nodes.expressions.networking;

import parser.nodes.ASTVisitor;
import parser.nodes.classes.TypeReferenceNode;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class ConnectionNode implements ExpressionNode {
    public ExpressionNode address;
    public TypeReferenceNode protocolType;

    public ConnectionNode(ExpressionNode address, TypeReferenceNode protocolType) {
        this.address = address;
        this.protocolType = protocolType;
    }

    @Override
    public <D> void accept(ASTVisitor<D> visitor, D data) {
        ExpressionNode.super.accept(visitor, data);

        address.accept(visitor, data);
        protocolType.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConnectionNode that)) return false;
        return Objects.equals(address, that.address) && Objects.equals(protocolType, that.protocolType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, protocolType);
    }

    @Override
    public String toString() {
        return address + " ~ " + protocolType;
    }
}
