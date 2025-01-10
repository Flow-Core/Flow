package parser.nodes.variable;

import parser.nodes.ExpressionNode;

public record VariableReferenceNode(
    String variable
) implements ExpressionNode {}
