package parser.nodes.classes;

import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.ArgumentNode;

import java.util.List;

public class ObjectNode implements ExpressionNode {
    public String className;
    public List<ArgumentNode> arguments;

    public ObjectNode(String className, List<ArgumentNode> arguments) {
        this.className = className;
        this.arguments = arguments;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, D data) {
        ExpressionNode.super.accept(visitor, data);

        for (final ArgumentNode argument : arguments) {
            argument.accept(visitor, data);
        }
    }

    @Override
    public String toString() {
        return "ObjectNode{" +
            "name='" + className + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
