package parser.nodes.literals;

public record Ipv4LiteralNode(
    String value
) implements LiteralNode { }
