package parser.nodes.variable;

import parser.nodes.ASTNode;

public record VariableAssignmentNode(
    String variable,
    ASTNode value
) implements ASTNode {}
