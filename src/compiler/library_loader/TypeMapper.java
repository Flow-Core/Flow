package compiler.library_loader;

import parser.nodes.FlowType;

import java.util.HashMap;
import java.util.Map;

public class TypeMapper {
    private static final Map<String, FlowType> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("void", new FlowType("Void", false, true));
        TYPE_MAP.put("int", new FlowType("Int", false, true));
        TYPE_MAP.put("boolean", new FlowType("Bool", false, true));
        TYPE_MAP.put("float", new FlowType("Float", false, true));
        TYPE_MAP.put("double", new FlowType("Double", false, true));
        TYPE_MAP.put("long", new FlowType("Long", false, true));
        TYPE_MAP.put("byte", new FlowType("Byte", false, true));
        TYPE_MAP.put("char", new FlowType("Char", false, true));
        TYPE_MAP.put("short", new FlowType("Short", false, true));
    }

    public static FlowType mapType(String className) {
        return TYPE_MAP.getOrDefault(className, new FlowType(className.replace("/", "."), false, false));
    }
}