package parser.nodes.literals;

public class Ipv4LiteralNode implements LiteralNode {
    public String value;

    public Ipv4LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Ipv4LiteralNode{" +
            "value='" + value + '\'' +
            '}';
    }
}
