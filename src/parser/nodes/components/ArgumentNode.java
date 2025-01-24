package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;

public class ArgumentNode implements ASTNode {
    public String name;
    public ExpressionNode value;

    public ArgumentNode(String name, ExpressionNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        value.accept(visitor);
    }

    @Override
    public String toString() {
        return "ArgumentNode{" +
            "name='" + name + '\'' +
            ", value=" + value +
            '}';
    }
}