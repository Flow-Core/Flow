package generators.ast.expressions;

import parser.nodes.expressions.ExpressionNode;
import parser.nodes.expressions.UnaryOperatorNode;

public class UnaryOperatorNodeGenerator {
    private ExpressionNode operand;
    private String operator = "!";
    private boolean isPostfix;

    public static UnaryOperatorNodeGenerator builder() {
        return new UnaryOperatorNodeGenerator();
    }

    public UnaryOperatorNodeGenerator operand(ExpressionNode operand) {
        this.operand = operand;
        return this;
    }

    public UnaryOperatorNodeGenerator operator(String operator) {
        this.operator = operator;
        return this;
    }

    public UnaryOperatorNodeGenerator isPostfix(boolean isPostfix) {
        this.isPostfix = isPostfix;
        return this;
    }

    public UnaryOperatorNode build() {
        return new UnaryOperatorNode(operand, operator, isPostfix);
    }
}