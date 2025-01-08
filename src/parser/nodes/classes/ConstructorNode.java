package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public record ConstructorNode(
    String accessModifier,
    List<ParameterNode> parameters,
    BlockNode body
) implements ASTNode {}
