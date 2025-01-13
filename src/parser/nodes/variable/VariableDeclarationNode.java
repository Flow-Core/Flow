package parser.nodes.variable;

import parser.nodes.ASTNode;

public class VariableDeclarationNode implements ASTNode {
    public String modifier;
    public String type;
    public String name;

    public VariableDeclarationNode(String modifier, String type, String name) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
    }
}
