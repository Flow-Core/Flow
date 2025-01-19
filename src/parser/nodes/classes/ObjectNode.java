package parser.nodes.classes;

import parser.nodes.ExpressionNode;
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
    public String toString() {
        return "ObjectNode{" +
            "name='" + className + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
