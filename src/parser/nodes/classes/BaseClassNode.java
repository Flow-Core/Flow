package parser.nodes.classes;

import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionNode;

import java.util.List;
import java.util.Objects;

public class BaseClassNode implements ExpressionNode {
    public FlowType type;
    public List<ArgumentNode> arguments;

    public BaseClassNode(FlowType type, List<ArgumentNode> arguments) {
        this.type = type;
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

        BaseClassNode that = (BaseClassNode) o;

        if (!Objects.equals(type, that.type)) return false;
        return Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, arguments);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(type + "(");

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
