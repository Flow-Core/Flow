package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

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
    public String toString() {
        return "ParameterNode{" +
            "type='" + type + '\'' +
            ", isNullable=" + isNullable +
            ", name='" + name + '\'' +
            ", defaultValue=" + defaultValue +
            '}';
    }
}
