package parser.nodes.literals;

public class FloatLiteralNode implements LiteralNode {
    public float value;

    public FloatLiteralNode(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FloatLiteralNode{" +
            "value=" + value +
            '}';
    }
}
