package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class VariableAssignmentNode implements ASTNode {
    public ExpressionBaseNode variable;
    public String operator;
    public ExpressionBaseNode value;

    public VariableAssignmentNode(ExpressionBaseNode variable, String operator, ExpressionBaseNode value) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        value.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableAssignmentNode that = (VariableAssignmentNode) o;

        if (!Objects.equals(variable, that.variable)) return false;
        if (!Objects.equals(operator, that.operator)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = variable != null ? variable.hashCode() : 0;
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return variable + " " + operator + " " + value;
    }
}
