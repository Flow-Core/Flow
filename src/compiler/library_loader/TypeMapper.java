package compiler.library_loader;

import java.util.HashMap;
import java.util.Map;

public class TypeMapper {
    private static final Map<String, TypeInfo> TYPE_MAP = new HashMap<>();

    static {
        // Primitives
        TYPE_MAP.put("void", new TypeInfo("Void", false));
        TYPE_MAP.put("int", new TypeInfo("Int", false));
        TYPE_MAP.put("boolean", new TypeInfo("Bool", false));
        TYPE_MAP.put("float", new TypeInfo("Float", false));
        TYPE_MAP.put("double", new TypeInfo("Double", false));
        TYPE_MAP.put("long", new TypeInfo("Long", false));
        TYPE_MAP.put("byte", new TypeInfo("Byte", false));
        TYPE_MAP.put("char", new TypeInfo("Char", false));
        TYPE_MAP.put("short", new TypeInfo("Short", false));

        // Java Boxed Types
        TYPE_MAP.put("java.lang.Void", new TypeInfo("Void", true));
        TYPE_MAP.put("java.lang.String", new TypeInfo("String", true));
        TYPE_MAP.put("java.lang.Integer", new TypeInfo("Int", true));
        TYPE_MAP.put("java.lang.Boolean", new TypeInfo("Bool", true));
        TYPE_MAP.put("java.lang.Float", new TypeInfo("Float", true));
        TYPE_MAP.put("java.lang.Double", new TypeInfo("Double", true));
        TYPE_MAP.put("java.lang.Long", new TypeInfo("Long", true));
        TYPE_MAP.put("java.lang.Byte", new TypeInfo("Byte", true));
        TYPE_MAP.put("java.lang.Character", new TypeInfo("Char", true));
        TYPE_MAP.put("java.lang.Short", new TypeInfo("Short", true));

        // Kotlin Types
        TYPE_MAP.put("kotlin.Unit", new TypeInfo("Void", true));
        TYPE_MAP.put("kotlin.String", new TypeInfo("String", true));
        TYPE_MAP.put("kotlin.Int", new TypeInfo("Int", true));
        TYPE_MAP.put("kotlin.Boolean", new TypeInfo("Bool", true));
        TYPE_MAP.put("kotlin.Float", new TypeInfo("Float", true));
        TYPE_MAP.put("kotlin.Double", new TypeInfo("Double", true));
        TYPE_MAP.put("kotlin.Long", new TypeInfo("Long", true));
        TYPE_MAP.put("kotlin.Byte", new TypeInfo("Byte", true));
        TYPE_MAP.put("kotlin.Char", new TypeInfo("Char", true));
        TYPE_MAP.put("kotlin.Short", new TypeInfo("Short", true));
    }

    public static TypeInfo mapType(String className) {
        return TYPE_MAP.getOrDefault(className, new TypeInfo(className.replace("/", "."), true));
    }

    public record TypeInfo(
        String flowType,
        boolean isNullable
    ) {}
}
