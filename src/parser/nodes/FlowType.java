package parser.nodes;

import parser.nodes.generics.TypeArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return !isPrimitive && !isNullable && isPrimitiveType() && typeArguments.isEmpty();
    }

    public boolean isPrimitiveType() {
        return switch (name) {
            case "Int", "Bool", "Float", "Double", "Long", "Byte", "Char", "Short" -> true;
            default -> false;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowType flowType = (FlowType) o;

        if (isNullable != flowType.isNullable) return false;
        if (isPrimitive != flowType.isPrimitive) return false;
        if (!Objects.equals(name, flowType.name)) return false;
        return Objects.equals(typeArguments, flowType.typeArguments);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (isNullable ? 1 : 0);
        result = 31 * result + (isPrimitive ? 1 : 0);
        result = 31 * result + (typeArguments != null ? typeArguments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(isPrimitive ? name.toLowerCase() : name);

        if (!typeArguments.isEmpty()) {
            sb.append("<");

            for (final TypeArgument typeArgument : typeArguments) {
                sb.append(typeArgument).append(", ");
            }

            sb.delete(sb.length() - 2, sb.length()).append(">");
        }

        sb.append(isNullable ? "?" : "");

        return sb.toString();
    }
}