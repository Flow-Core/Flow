package parser.nodes.variable;

import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class FieldReferenceNode implements ExpressionNode {
    public FlowType holderType;
    public String name;
    public ExpressionNode holder;
    public FlowType type;
    public boolean isStatic;

    public FieldReferenceNode(
        FlowType holderType,
        String name,
        ExpressionNode holder,
        FlowType type,
        boolean isStatic
    ) {
        this.holderType = holderType;
        this.name = name;
        this.holder = holder;
        this.type = type;
        this.isStatic = isStatic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldReferenceNode that = (FieldReferenceNode) o;

        if (isStatic != that.isStatic) return false;
        if (!Objects.equals(holderType, that.holderType)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(holder, that.holder)) return false;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        int result = holderType != null ? holderType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (holder != null ? holder.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (isStatic ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return holderType + (isStatic ? "()" : "") + "." + name + ": " + type;
    }
}
