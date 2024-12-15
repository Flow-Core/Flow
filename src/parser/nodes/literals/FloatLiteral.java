package parser.nodes.literals;

public class FloatLiteral implements LiteralNode {
    private final float value;

    public FloatLiteral(final float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}
