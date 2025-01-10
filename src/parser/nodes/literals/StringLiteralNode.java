package parser.nodes.literals;

public record StringLiteralNode(
    String value
) implements LiteralNode {}