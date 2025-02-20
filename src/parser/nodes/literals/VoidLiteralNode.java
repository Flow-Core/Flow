package parser.nodes.literals;

public class VoidLiteralNode implements LiteralNode {
    @Override
    public String getClassName() {
        return "Void";
    }

    @Override
    public Object getValue() {
        return null;
    }
}
