package parser.nodes.classes;

import parser.nodes.ASTNode;

import java.util.List;

public record ClassDeclarationNode(
    String name,
    List<String> modifier,
    String baseClass,
    List<String> interfaces,
    List<ASTNode> fields,
    List<ASTNode> methods,
    List<ConstructorNode> constructors
) implements ASTNode {}
