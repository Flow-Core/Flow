package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;

public record ParameterNode(
    String type,
    String name,
    ExpressionNode defaultValue
) implements ASTNode {}
