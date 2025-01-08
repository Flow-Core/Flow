package parser.nodes.variable;

public record VariableParseResult(
    VariableDeclarationNode declaration,
    VariableAssignmentNode assignment
) {}
