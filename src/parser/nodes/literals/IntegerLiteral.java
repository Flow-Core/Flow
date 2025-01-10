package parser.nodes.literals;

public record IntegerLiteral(
    int value
) implements LiteralNode {}