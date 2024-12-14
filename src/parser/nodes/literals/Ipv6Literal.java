package parser.nodes.literals;

import parser.nodes.ASTNode;

public class Ipv6Literal implements ASTNode {
    private final String value;

    public Ipv6Literal(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
