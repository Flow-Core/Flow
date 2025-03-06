package parser.nodes.functions;

import parser.nodes.ASTVisitor;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionNode;

import java.util.List;
import java.util.Objects;

public class FunctionCallNode implements ExpressionNode {
    public String callerType;
    public ExpressionNode caller;
    public boolean isSafeCall;
    public String name;
    public List<ArgumentNode> arguments;

    public FunctionCallNode(String name, List<ArgumentNode> arguments) {
        this.name = name;
        this.arguments = arguments;
        this.caller = null;
        this.callerType = null;
        this.isSafeCall = false;
    }

    public FunctionCallNode(String callerType, ExpressionNode caller, boolean isSafeCall, String name, List<ArgumentNode> arguments) {
        this.callerType = callerType;
        this.caller = caller;
        this.isSafeCall = isSafeCall;
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ExpressionNode.super.accept(visitor, data);

        for (final ArgumentNode argument : arguments) {
            argument.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionCallNode that = (FunctionCallNode) o;

        if (!Objects.equals(callerType, that.callerType)) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = callerType != null ? callerType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (callerType != null) {
            sb.append(callerType).append(".");
        }

        sb.append(name).append("(");

        for (ArgumentNode argumentNode : arguments) {
            sb.append(argumentNode).append(", ");
        }

        if (!arguments.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");

        return sb.toString();
    }
}
