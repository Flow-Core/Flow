package parser.nodes;

public record FlowType(
    String name,
    boolean isNullable,
    boolean isPrimitive
) {}
