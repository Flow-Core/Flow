package parser.nodes.classes;

import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.ArgumentNode;

import java.util.List;
import java.util.Objects;

public class ObjectNode implements ExpressionNode {
    public String className;
    public List<ArgumentNode> arguments;

    public ObjectNode(String className, List<ArgumentNode> arguments) {
        this.className = className;
        this.arguments = arguments;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, D data) {
        ExpressionNode.super.accept(visitor, data);

        for (final ArgumentNode argument : arguments) {
            argument.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectNode that = (ObjectNode) o;

        if (!Objects.equals(className, that.className)) return false;
        return Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(className + "(");

        for (ArgumentNode argumentNode : arguments) {
            sb.append(argumentNode).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");

        return sb.toString();
    }
}
