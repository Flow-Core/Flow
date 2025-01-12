package parser.nodes;

import parser.nodes.components.ParameterNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public record FunctionDeclarationNode(
    String name,
    String returnType,
    List<String> modifiers,
    List<ParameterNode> parameters,
    BlockNode block
) implements ASTNode {}
