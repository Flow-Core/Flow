package parser.nodes.components;

import parser.nodes.ASTNode;

public class ArgumentNode implements ASTNode {
    private final String type;
    private final String name;

    public ArgumentNode(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
