package parser.nodes.variable;

import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class VariableReferenceNode implements ExpressionNode {
    public String variable;

    public VariableReferenceNode(String variable) {
        this.variable = variable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableReferenceNode that = (VariableReferenceNode) o;

        return Objects.equals(variable, that.variable);
    }

    @Override
    public int hashCode() {
        return variable != null ? variable.hashCode() : 0;
    }

    @Override
    public String toString() {
        return variable;
    }
}
