package parser.nodes.literals;

public class Ipv4LiteralNode implements LiteralNode {
    public String value;

    public Ipv4LiteralNode(String value) {
        this.value = value;
    }
}
