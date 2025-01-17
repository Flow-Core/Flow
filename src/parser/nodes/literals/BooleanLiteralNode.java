package parser.nodes.literals;

public class BooleanLiteralNode implements LiteralNode {
    public boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BooleanLiteralNode{" +
            "value=" + value +
            '}';
    }
}
