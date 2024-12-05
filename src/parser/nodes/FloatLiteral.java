package parser.nodes;

public class FloatLiteral implements ASTNode {
    private final float value;

    public FloatLiteral(final float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
