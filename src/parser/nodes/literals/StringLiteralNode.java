package parser.nodes.literals;

import java.util.Objects;

public class StringLiteralNode implements LiteralNode {
    public String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "String";
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringLiteralNode that = (StringLiteralNode) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
