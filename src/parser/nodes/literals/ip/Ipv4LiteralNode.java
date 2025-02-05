package parser.nodes.literals.ip;

import java.util.Objects;

public class Ipv4LiteralNode extends IpLiteral {
    public String value;

    public Ipv4LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ipv4LiteralNode that = (Ipv4LiteralNode) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Ipv4LiteralNode{" +
            "value='" + value + '\'' +
            '}';
    }
}
