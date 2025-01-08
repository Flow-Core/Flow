package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FunctionDeclarationNode;

import java.util.List;

public record ClassDeclarationNode(
    String name,
    List<String> modifier,
    String baseClass,
    List<String> interfaces,
    List<FieldNode> fields,
    List<FunctionDeclarationNode> methods,
    List<ConstructorNode> constructors
) implements ASTNode {}
