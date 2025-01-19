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
    public String toString() {
        return "IntegerLiteralNode{" +
            "value=" + value +
            '}';
    }
}
