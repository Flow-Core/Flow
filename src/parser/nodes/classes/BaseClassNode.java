package parser.nodes.classes;

import parser.nodes.components.ArgumentNode;

import java.util.List;

public class BaseClassNode {
    public String name;
    public List<ArgumentNode> arguments;

    public BaseClassNode(String name, List<ArgumentNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "BaseClassNode{" +
            "name='" + name + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
