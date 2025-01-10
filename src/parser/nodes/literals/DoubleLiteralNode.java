package parser.nodes.literals;

public record DoubleLiteralNode(
    double value
) implements LiteralNode {}
