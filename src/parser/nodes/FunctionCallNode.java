package parser.nodes;

import parser.nodes.components.ArgumentNode;

import java.util.List;

public record FunctionCallNode(
    String name,
    List<ArgumentNode> arguments
) implements ExpressionNode {
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Function call " + name + ":\n");

        for (final ArgumentNode node : arguments) {
            output.append(node.toString()).append("\n");
        }

        return output.toString();
    }
}
