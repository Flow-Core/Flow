package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionBaseNode;

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
    public String toString() {
        return "VariableAssignmentNode{" +
            "variable='" + variable + '\'' +
            ", operator='" + operator + '\'' +
            ", value=" + value +
            '}';
    }
}
