package parser.nodes.literals;

public record StringLiteral(
    String value
) implements LiteralNode {}