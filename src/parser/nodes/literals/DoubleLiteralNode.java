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
    public String toString() {
        return "DoubleLiteralNode{" +
            "value=" + value +
            '}';
    }
}
