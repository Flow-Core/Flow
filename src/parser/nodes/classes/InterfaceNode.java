package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FunctionDeclarationNode;

import java.util.List;

public record InterfaceNode(
    String name,
    List<String> modifiers,
    List<String> implementedInterfaces,
    List<FunctionDeclarationNode> methods
) implements ASTNode {}
