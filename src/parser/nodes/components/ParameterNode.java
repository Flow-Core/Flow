package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class ParameterNode implements ASTNode {
    public String type;
    public boolean isNullable;
    public String name;
    public ExpressionBaseNode defaultValue;

    public ParameterNode(String type, boolean isNullable, String name, ExpressionBaseNode defaultValue) {
        this.type = type;
        this.isNullable = isNullable;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, D data) {
        ASTNode.super.accept(visitor, data);

        if (defaultValue != null) {
            defaultValue.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterNode that = (ParameterNode) o;

        if (isNullable != that.isNullable) return false;
        if (!Objects.equals(type, that.type)) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (isNullable ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParameterNode{" +
            "type='" + type + '\'' +
            ", isNullable=" + isNullable +
            ", name='" + name + '\'' +
            ", defaultValue=" + defaultValue +
            '}';
    }
}
