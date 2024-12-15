package parser.nodes.variable;

import parser.nodes.ASTNode;

public class VariableAssignment implements ASTNode {
    private final String variable;
    private final ASTNode value;

    public VariableAssignment(String variable, ASTNode value) {
        this.variable = variable;
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "VariableAssignment(variable=" + variable + ", value=" + value + ")";
    }
}
