package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class ParameterNode implements ASTNode {
    public FlowType type;
    public String name;
    public ExpressionBaseNode defaultValue;

    public ParameterNode(FlowType type, String name, ExpressionBaseNode defaultValue) {
        this.type = type;
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

        if (!Objects.equals(type, that.type)) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (defaultValue != null) {
            return name + ": " + type + " = " + defaultValue;
        } else {
            return name + ": " + type;
        }
    }
}
