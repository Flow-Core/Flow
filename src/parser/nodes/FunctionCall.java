package parser.nodes;

import java.util.List;

public class FunctionCall implements ExpressionNode {
    private final String name;
    private final List<ExpressionNode> arguments;

    public FunctionCall(final String name, final List<ExpressionNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Function call " + name + ":\n");

        for (ExpressionNode node : arguments) {
            output.append(node.toString()).append("\n");
        }

        return output.toString();
    }
}
