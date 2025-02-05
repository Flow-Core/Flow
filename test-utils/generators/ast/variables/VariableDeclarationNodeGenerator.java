package generators.ast.variables;

import parser.nodes.variable.VariableDeclarationNode;

public class VariableDeclarationNodeGenerator {
    private String modifier = "val";
    private String type;
    private String name;
    private boolean isNullable = false;

    public static VariableDeclarationNodeGenerator builder() {
        return new VariableDeclarationNodeGenerator();
    }

    public VariableDeclarationNodeGenerator modifier(String modifier) {
        this.modifier = modifier;
        return this;
    }

    public VariableDeclarationNodeGenerator type(String type) {
        this.type = type;
        return this;
    }

    public VariableDeclarationNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public VariableDeclarationNodeGenerator isNullable(boolean isNullable) {
        this.isNullable = isNullable;
        return this;
    }

    public VariableDeclarationNode build() {
        return new VariableDeclarationNode(modifier, type, name, isNullable);
    }
}