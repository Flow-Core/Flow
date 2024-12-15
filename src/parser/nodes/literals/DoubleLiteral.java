package parser.nodes.literals;

public class DoubleLiteral implements LiteralNode {
    private final double value;

    public DoubleLiteral(final double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
