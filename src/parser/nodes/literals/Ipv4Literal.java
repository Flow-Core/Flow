package parser.nodes.literals;

public record Ipv4Literal(
    String value
) implements LiteralNode { }
