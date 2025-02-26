package parser.nodes;

public class FlowType {
    public String name;
    public boolean isNullable;
    public boolean isPrimitive;

    public FlowType(String name, boolean isNullable, boolean isPrimitive) {
        this.name = name;
        this.isNullable = isNullable;
        this.isPrimitive = isPrimitive;
    }

    public boolean shouldBePrimitive() {
        return !isPrimitive && !isNullable && isPrimitiveType();
    }

    public boolean isPrimitiveType() {
        return switch (name) {
            case "Int", "Bool", "Float", "Double", "Long", "Byte", "Char", "Short" -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return (isPrimitive ? name.toLowerCase() : name) + (isNullable ? "?" : "");
    }
}