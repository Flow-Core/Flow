package parser.nodes.literals;

public class BooleanLiteralNode implements LiteralNode {
    public boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "Bool";
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanLiteralNode that = (BooleanLiteralNode) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }
}
