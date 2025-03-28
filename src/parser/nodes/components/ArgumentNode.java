package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class ArgumentNode implements ASTNode {
    public String name;
    public ExpressionBaseNode value;
    public FlowType type;

    public ArgumentNode(String name, ExpressionBaseNode value) {
        this.name = name;
        this.value = value;
        type = null;
    }

    public ArgumentNode(String name, ExpressionBaseNode value, FlowType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        value.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArgumentNode that = (ArgumentNode) o;

        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (type != null) {
            return name + ": " + type + " = " + value;
        } else if (name != null) {
            return name + " = " + value;
        } else {
            return value.toString();
        }
    }
}