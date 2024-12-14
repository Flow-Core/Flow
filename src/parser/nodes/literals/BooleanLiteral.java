package parser.nodes.literals;

public class BooleanLiteral implements LiteralNode {
    private final boolean value;

    public BooleanLiteral(final boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
