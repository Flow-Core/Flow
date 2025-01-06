package parser.nodes.variable;

import parser.nodes.ASTNode;

public class VariableDeclaration extends VariableAssignment implements ASTNode {
    private final String modifier;
    private final String type;
    private final String name;
    private final ASTNode value;

    public VariableDeclaration(String modifier, String type, String name, ASTNode value) {
        super(name, value);

        this.modifier = modifier;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getModifier() {
        return modifier;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "VariableDeclaration(modifier=" + modifier + ", name=" + name + ", type=" + type + ", value=" + value + ")";
    }
}
