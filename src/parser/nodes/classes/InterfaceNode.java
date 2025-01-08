package parser.nodes.classes;

import parser.nodes.ASTNode;

import java.util.List;

public record InterfaceNode(
    String name,
    List<String> modifiers,
    List<String> implementedInterfaces,
    List<MethodSignatureNode> methods
) implements ASTNode {}
