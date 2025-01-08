package parser.nodes;

import java.util.List;

public record ClassDeclarationNode(
    String name,
    String baseClass,
    List<ASTNode> fields,
    List<ASTNode> methods
) implements ASTNode {}
