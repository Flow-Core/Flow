package parser.nodes.literals;

public class CharLiteralNode implements LiteralNode {
    public char value;

    public CharLiteralNode(char value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return "Char";
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharLiteralNode that = (CharLiteralNode) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return Character.toString(value);
    }
}
