package parser.nodes.literals;

public class DoubleLiteralNode implements LiteralNode {
    public double value;

    public DoubleLiteralNode(double value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "Double";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleLiteralNode that = (DoubleLiteralNode) o;

        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public String toString() {
        return "DoubleLiteralNode{" +
            "value=" + value +
            '}';
    }
}
