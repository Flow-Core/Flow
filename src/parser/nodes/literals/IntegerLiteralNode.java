package parser.nodes.literals;

public record IntegerLiteralNode(
    int value
) implements LiteralNode {}