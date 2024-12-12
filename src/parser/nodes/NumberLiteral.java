package parser.nodes;

public class NumberLiteral implements LiteralNode {
    private final int value;

    public NumberLiteral(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}