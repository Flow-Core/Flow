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
        StringBuilder output = new StringBuilder("Function call " + name + ":\n");

        for (final ArgumentNode node : arguments) {
            output.append(node.toString()).append("\n");
        }

        return output.toString();
    }
}
