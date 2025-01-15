package parser.nodes.classes;

import parser.nodes.ExpressionNode;
import parser.nodes.components.ArgumentNode;

import java.util.List;

public class ObjectNode implements ExpressionNode {
    public String name;
    public List<ArgumentNode> arguments;

    public ObjectNode(String name, List<ArgumentNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "ObjectNode{" +
            "name='" + name + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
