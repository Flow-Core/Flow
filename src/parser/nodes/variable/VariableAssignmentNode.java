package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionBaseNode;

public class VariableAssignmentNode implements ASTNode {
    public String variable;
    public String operator;
    public ExpressionBaseNode value;

    public VariableAssignmentNode(String variable, String operator, ExpressionBaseNode value) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        value.accept(visitor);
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
