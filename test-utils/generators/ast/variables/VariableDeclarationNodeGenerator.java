package generators.ast.variables;

import parser.nodes.FlowType;
import parser.nodes.variable.VariableDeclarationNode;

public class VariableDeclarationNodeGenerator {
    private String modifier = "val";
    private FlowType type;
    private String name;

    public static VariableDeclarationNodeGenerator builder() {
        return new VariableDeclarationNodeGenerator();
    }

    public VariableDeclarationNodeGenerator modifier(String modifier) {
        this.modifier = modifier;
        return this;
    }

    public VariableDeclarationNodeGenerator type(FlowType type) {
        this.type = type;
        return this;
    }

    public VariableDeclarationNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public VariableDeclarationNode build() {
        return new VariableDeclarationNode(modifier, type, name);
    }
}