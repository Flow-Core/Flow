package parser.nodes.generics;

import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class TypeArgument implements ExpressionNode {
    public FlowType type;

    public TypeArgument(FlowType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeArgument that = (TypeArgument) o;

        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
