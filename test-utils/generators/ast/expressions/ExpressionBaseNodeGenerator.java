package generators.ast.expressions;

import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;

public class ExpressionBaseNodeGenerator {
    private ExpressionNode expression;

    public static ExpressionBaseNodeGenerator builder() {
        return new ExpressionBaseNodeGenerator();
    }

    public ExpressionBaseNodeGenerator expression(ExpressionNode expression) {
        this.expression = expression;
        return this;
    }

    public ExpressionBaseNode build() {
        return new ExpressionBaseNode(expression);
    }
}
