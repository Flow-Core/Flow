package parser.nodes;

import parser.nodes.generics.TypeArgument;

import java.util.ArrayList;
import java.util.List;

public class FlowType {
    public String name;
    public boolean isNullable;
    public boolean isPrimitive;
    public List<TypeArgument> typeArguments;

    public FlowType(String name, boolean isNullable, boolean isPrimitive) {
        this.name = name;
        this.isNullable = isNullable;
        this.isPrimitive = isPrimitive;

        typeArguments = new ArrayList<>();
    }

    public FlowType(String name, boolean isNullable, boolean isPrimitive, List<TypeArgument> typeArguments) {
        this.name = name;
        this.isNullable = isNullable;
        this.isPrimitive = isPrimitive;
        this.typeArguments = typeArguments;
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