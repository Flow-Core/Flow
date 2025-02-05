package parser.nodes.literals.ip;

import java.util.Objects;

public class Ipv6LiteralNode extends IpLiteral {
    public String value;

    public Ipv6LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ipv6LiteralNode that = (Ipv6LiteralNode) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Ipv6LiteralNode{" +
            "value='" + value + '\'' +
            '}';
    }
}

