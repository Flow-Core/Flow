package parser.nodes;

import java.util.List;

public record FunctionCall(
    String name,
    List<ExpressionNode> arguments
) implements ExpressionNode {
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Function call " + name + ":\n");

        for (ExpressionNode node : arguments) {
            output.append(node.toString()).append("\n");
        }

        return output.toString();
    }
}
