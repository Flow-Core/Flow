package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;

public record ArgumentNode(
    String name,
    ExpressionNode value
) implements ASTNode {}