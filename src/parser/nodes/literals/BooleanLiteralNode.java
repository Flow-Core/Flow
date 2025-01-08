package parser.nodes.literals;

public record BooleanLiteralNode(
    boolean value
) implements LiteralNode {}
