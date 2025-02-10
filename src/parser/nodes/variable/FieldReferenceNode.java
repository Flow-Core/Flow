package parser.nodes.variable;

import parser.nodes.expressions.ExpressionNode;
import semantic_analysis.visitors.ExpressionTraverse.TypeWrapper;

import java.util.Objects;

public class FieldReferenceNode implements ExpressionNode {
    public String holderType;
    public String name;
    public ExpressionNode holder;
    public TypeWrapper type;

    public FieldReferenceNode(
        String holderType,
        String name,
        ExpressionNode holder,
        TypeWrapper type
    ) {
        this.holderType = holderType;
        this.name = name;
        this.holder = holder;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldReferenceNode that = (FieldReferenceNode) o;

        if (!Objects.equals(holderType, that.holderType)) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(holder, that.holder);
    }

    @Override
    public int hashCode() {
        int result = holderType != null ? holderType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (holder != null ? holder.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldReferenceNode{" +
            "holderType='" + holderType + '\'' +
            ", name='" + name + '\'' +
            ", holder=" + holder +
            '}';
    }
}
