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

    @Override
    public String toString() {
        return (isPrimitive ? name.toLowerCase() : name) + (isNullable ? "?" : "");
    }
}
