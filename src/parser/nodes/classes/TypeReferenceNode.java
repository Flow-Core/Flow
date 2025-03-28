package parser.nodes.classes;

import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class TypeReferenceNode implements ExpressionNode {
    public FlowType type;

    public TypeReferenceNode(
        FlowType type
    ) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TypeReferenceNode that = (TypeReferenceNode) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
