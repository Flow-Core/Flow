package parser.nodes.literals;

public class LongLiteralNode implements LiteralNode {
    public long value;

    public LongLiteralNode(long value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "Long";
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongLiteralNode that = (LongLiteralNode) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public String toString() {
        return value + "L";
    }
}
