package generators.ast.expressions;

import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionNode;

public class BinaryExpressionNodeGenerator {
    private ExpressionNode left;
    private ExpressionNode right;
    private String operator = "+";

    public static BinaryExpressionNodeGenerator builder() {
        return new BinaryExpressionNodeGenerator();
    }

    public BinaryExpressionNodeGenerator left(ExpressionNode left) {
        this.left = left;
        return this;
    }

    public BinaryExpressionNodeGenerator right(ExpressionNode right) {
        this.right = right;
        return this;
    }

    public BinaryExpressionNodeGenerator operator(String operator) {
        this.operator = operator;
        return this;
    }

    public BinaryExpressionNode build() {
        return new BinaryExpressionNode(left, right, operator);
    }
}
