package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;

import java.util.List;

public record ClassDeclarationNode(
    String name,
    List<ParameterNode> primaryConstructor,
    String baseClass,
    List<ArgumentNode> baseClassArguments,
    List<String> interfaces,
    List<FieldNode> fields,
    List<FunctionDeclarationNode> methods,
    List<ConstructorNode> constructors,
    BlockNode initBlock
) implements ASTNode {}