package parser.nodes;

public class StringLiteral implements LiteralNode {
    private final String value;

    public StringLiteral(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}