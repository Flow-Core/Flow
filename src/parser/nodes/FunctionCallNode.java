package parser.nodes;

import parser.nodes.components.ArgumentNode;

import java.util.List;

public class FunctionCallNode implements ExpressionNode {
    public String name;
    public List<ArgumentNode> arguments;

    public FunctionCallNode(String name, List<ArgumentNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "FunctionCallNode{" +
            "name='" + name + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
