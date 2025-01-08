package parser.nodes;

import parser.nodes.components.ArgumentNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public record FunctionDeclarationNode(
    String name,
    String returnType,
    List<ArgumentNode> args,
    BlockNode block
) implements ASTNode {}
