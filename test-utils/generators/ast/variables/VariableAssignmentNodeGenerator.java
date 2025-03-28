package generators.ast.variables;

import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.variable.VariableAssignmentNode;

public class VariableAssignmentNodeGenerator {
    private ExpressionBaseNode variable;
    private String operator = "=";
    private ExpressionBaseNode value;

    public static VariableAssignmentNodeGenerator builder() {
        return new VariableAssignmentNodeGenerator();
    }

    public VariableAssignmentNodeGenerator variable(ExpressionBaseNode variable) {
        this.variable = variable;
        return this;
    }

    public VariableAssignmentNodeGenerator operator(String operator) {
        this.operator = operator;
        return this;
    }

    public VariableAssignmentNodeGenerator value(ExpressionBaseNode value) {
        this.value = value;
        return this;
    }

    public VariableAssignmentNode build() {
        return new VariableAssignmentNode(variable, operator, value);
    }
}