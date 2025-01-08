package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.variable.InitializedVariable;

import java.util.List;

public record FieldNode(
    List<String> modifiers,
    InitializedVariable initialization
) implements ASTNode {}
