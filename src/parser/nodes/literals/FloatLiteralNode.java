package parser.nodes.literals;

public class FloatLiteralNode implements LiteralNode {
    public float value;

    public FloatLiteralNode(float value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "Float";
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FloatLiteralNode that = (FloatLiteralNode) o;

        return Float.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return (value != 0.0f ? Float.floatToIntBits(value) : 0);
    }

    @Override
    public String toString() {
        return value + "f";
    }
}
