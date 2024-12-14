package parser.nodes.literals;

public class IntegerLiteral implements LiteralNode {
    private final int value;

    public IntegerLiteral(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}