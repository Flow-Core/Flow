package parser.nodes.literals;

public class Ipv6LiteralNode implements LiteralNode {
    public String value;

    public Ipv6LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Ipv6LiteralNode{" +
            "value='" + value + '\'' +
            '}';
    }
}

