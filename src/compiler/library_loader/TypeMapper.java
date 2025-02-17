package compiler.library_loader;

import java.util.HashMap;
import java.util.Map;

public class TypeMapper {
    private static final Map<String, TypeInfo> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("void", new TypeInfo("Void", false));
        TYPE_MAP.put("int", new TypeInfo("Int", false));
        TYPE_MAP.put("boolean", new TypeInfo("Bool", false));
        TYPE_MAP.put("float", new TypeInfo("Float", false));
        TYPE_MAP.put("double", new TypeInfo("Double", false));
        TYPE_MAP.put("long", new TypeInfo("Long", false));
        TYPE_MAP.put("byte", new TypeInfo("Byte", false));
        TYPE_MAP.put("char", new TypeInfo("Char", false));
        TYPE_MAP.put("short", new TypeInfo("Short", false));
    }

    public static TypeInfo mapType(String className) {
        return TYPE_MAP.getOrDefault(className, new TypeInfo(className.replace("/", "."), true));
    }

    public record TypeInfo(
        String flowType,
        boolean isNullable
    ) {}
}