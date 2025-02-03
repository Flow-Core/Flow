package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

public class ArgumentNode implements ASTNode {
    public String name;
    public ExpressionBaseNode value;

    public ArgumentNode(String name, ExpressionBaseNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        value.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "ArgumentNode{" +
            "name='" + name + '\'' +
            ", value=" + value +
            '}';
    }
}