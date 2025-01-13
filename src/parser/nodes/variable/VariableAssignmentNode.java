package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;

public class VariableAssignmentNode implements ASTNode {
    public String variable;
    public ExpressionNode value;

    public VariableAssignmentNode(String variable, ExpressionNode value) {
        this.variable = variable;
        this.value = value;
    }
}
