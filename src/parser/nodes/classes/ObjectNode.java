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
    public void accept(ASTVisitor visitor) {
        ExpressionNode.super.accept(visitor);

        for (final ArgumentNode argument : arguments) {
            argument.accept(visitor);
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
