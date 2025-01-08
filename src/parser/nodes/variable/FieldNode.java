package parser.nodes.variable;

import parser.nodes.ASTNode;

public record FieldNode(
    String accessModifier,
    InitializedVariable initialization
) implements ASTNode {}
