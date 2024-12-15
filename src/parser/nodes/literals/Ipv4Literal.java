package parser.nodes.literals;

public class Ipv4Literal implements LiteralNode {
    private final String value;

    public Ipv4Literal(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
