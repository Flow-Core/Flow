package parser.nodes.literals;

import parser.nodes.ASTNode;

public class Ipv4Literal implements ASTNode {
    private final String value;

    public Ipv4Literal(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
