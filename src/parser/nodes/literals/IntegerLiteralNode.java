package parser.nodes.literals;

public class IntegerLiteralNode implements LiteralNode {
    public int value;

    public IntegerLiteralNode(int value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "Int";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegerLiteralNode that = (IntegerLiteralNode) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "IntegerLiteralNode{" +
            "value=" + value +
            '}';
    }
}
