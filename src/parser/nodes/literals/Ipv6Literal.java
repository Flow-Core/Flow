package parser.nodes.literals;

public record Ipv6Literal(
    String value
) implements LiteralNode {}
