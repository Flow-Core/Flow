package parser.nodes.variable;

import parser.nodes.ASTNode;

public class VariableDeclarationNode implements ASTNode {
    public String modifier;
    public String type;
    public String name;
    public boolean isNullable;

    public VariableDeclarationNode(String modifier, String type, String name, boolean isNullable) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
        this.isNullable = isNullable;
    }

    @Override
    public String toString() {
        return "VariableDeclarationNode{" +
                "modifier='" + modifier + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", isNullable=" + isNullable +
                '}';
    }
}
