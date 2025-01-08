package parser.nodes.components;

import parser.nodes.ASTNode;

public record ArgumentNode(
    String type,
    String name
) implements ASTNode {}
