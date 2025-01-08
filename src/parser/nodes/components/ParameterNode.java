package parser.nodes.components;

import parser.nodes.ASTNode;

public record ParameterNode(
    String type,
    String name
) implements ASTNode {}
