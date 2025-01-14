package parser.nodes.literals;

public class StringLiteralNode implements LiteralNode {
    public String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StringLiteralNode{" +
            "value='" + value + '\'' +
            '}';
    }
}
