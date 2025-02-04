package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

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
    public String toString() {
        return "VariableAssignmentNode{" +
            "variable='" + variable + '\'' +
            ", operator='" + operator + '\'' +
            ", value=" + value +
            '}';
    }
}
