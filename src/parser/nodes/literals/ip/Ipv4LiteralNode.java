package parser.nodes.literals.ip;

public class Ipv4LiteralNode extends IpLiteral {
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
