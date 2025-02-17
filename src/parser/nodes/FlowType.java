package parser.nodes;

public record FlowType(
    String name,
    boolean isNullable,
    boolean isPrimitive
) {
    public FlowType copy(boolean isNullable, boolean isPrimitive) {
        return new FlowType(
            name,
            isNullable,
            isPrimitive
        );
    }

    public FlowType copy(boolean isPrimitive) {
        return new FlowType(
            name,
            isNullable,
            isPrimitive
        );
    }

    @Override
    public String toString() {
        return (isPrimitive ? name.toLowerCase() : name) + (isNullable ? "?" : "");
    }
}
