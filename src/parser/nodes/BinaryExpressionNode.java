package parser.nodes;

public record BinaryExpressionNode(
    ExpressionNode left,
    ExpressionNode right,
    String operator
) implements ExpressionNode { }
