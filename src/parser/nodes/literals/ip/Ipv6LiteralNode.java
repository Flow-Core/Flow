package parser.nodes.literals.ip;

public class Ipv6LiteralNode extends IpLiteral {
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

