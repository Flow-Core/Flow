package parser.nodes.variable;

import parser.nodes.ASTNode;

public record InitializedVariableNode(
    VariableDeclarationNode declaration,
    VariableAssignmentNode assignment
) implements ASTNode {}
