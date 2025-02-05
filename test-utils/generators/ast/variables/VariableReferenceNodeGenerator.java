package generators.ast.variables;

import parser.nodes.variable.VariableReferenceNode;

public class VariableReferenceNodeGenerator {
    private String variable;

    public static VariableReferenceNodeGenerator builder() {
        return new VariableReferenceNodeGenerator();
    }

    public VariableReferenceNodeGenerator variable(String variable) {
        this.variable = variable;
        return this;
    }

    public VariableReferenceNode build() {
        return new VariableReferenceNode(variable);
    }
}