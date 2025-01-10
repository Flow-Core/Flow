package parser.nodes.literals;

public record Ipv6LiteralNode(
    String value
) implements LiteralNode {}
