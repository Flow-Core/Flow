package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.components.ParameterNode;

import java.util.List;

public record MethodSignatureNode(
    String name,
    List<String> modifiers,
    List<ParameterNode> parameters,
    String returnType
) implements ASTNode {}
