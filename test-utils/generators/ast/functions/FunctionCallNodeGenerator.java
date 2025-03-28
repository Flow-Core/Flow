package generators.ast.functions;

import parser.nodes.FlowType;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallNodeGenerator {
    private FlowType callerType = null;
    private ExpressionNode caller = null;
    private boolean isSafeCall = false;
    private String name;
    private final List<ArgumentNode> arguments = new ArrayList<>();

    public static FunctionCallNodeGenerator builder() {
        return new FunctionCallNodeGenerator();
    }

    public FunctionCallNodeGenerator callerType(FlowType callerType) {
        this.callerType = callerType;
        return this;
    }

    public FunctionCallNodeGenerator caller(ExpressionNode caller) {
        this.caller = caller;
        return this;
    }

    public FunctionCallNodeGenerator isSafeCall(boolean isSafeCall) {
        this.isSafeCall = isSafeCall;
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
        return new FunctionCallNode(callerType, caller, isSafeCall, name, arguments);
    }
}