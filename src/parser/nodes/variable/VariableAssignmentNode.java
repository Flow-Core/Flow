package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;

public record VariableAssignmentNode(
    String variable,
    ExpressionNode value
) implements ASTNode {}
