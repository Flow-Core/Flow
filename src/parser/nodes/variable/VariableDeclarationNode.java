package parser.nodes.variable;

import parser.nodes.ASTNode;

public record VariableDeclarationNode(
    String modifier,
    String type,
    String name
) implements ASTNode {}
