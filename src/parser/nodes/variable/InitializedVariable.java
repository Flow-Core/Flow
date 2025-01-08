package parser.nodes.variable;

public record InitializedVariable(
    VariableDeclarationNode declaration,
    VariableAssignmentNode assignment
) {}
