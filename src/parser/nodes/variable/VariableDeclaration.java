package parser.nodes.variable;

import parser.nodes.ASTNode;

public class VariableDeclaration {
    private final String type;
    private final String name;
    private final ASTNode value;

    public VariableDeclaration(String type, String name, ASTNode value) {
        this.type = type;
        this.name = name;
        this.value = value;
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
}
