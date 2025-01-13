package parser.nodes.literals;

public class BooleanLiteralNode implements LiteralNode {
    public boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }
}
