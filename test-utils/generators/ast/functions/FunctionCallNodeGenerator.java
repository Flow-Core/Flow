package generators.ast.functions;

import parser.nodes.components.ArgumentNode;
import parser.nodes.functions.FunctionCallNode;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallNodeGenerator {
    private String callerType = null;
    private String name;
    private final List<ArgumentNode> arguments = new ArrayList<>();

    public static FunctionCallNodeGenerator builder() {
        return new FunctionCallNodeGenerator();
    }

    public FunctionCallNodeGenerator callerType(String callerType) {
        this.callerType = callerType;
        return this;
    }

    public FunctionCallNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public FunctionCallNodeGenerator addArgument(ArgumentNode argument) {
        this.arguments.add(argument);
        return this;
    }

    public FunctionCallNode build() {
        return new FunctionCallNode(callerType, name, arguments);
    }
}