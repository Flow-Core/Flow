package parser.nodes;

public record BinaryExpressionNode(
    ASTNode left,
    ASTNode right,
    String operator
) implements ExpressionNode { }
