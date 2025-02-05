package generators.ast.statements;

import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.statements.ThrowNode;

public class ThrowNodeGenerator {
    private ExpressionBaseNode throwValue;

    public static ThrowNodeGenerator builder() {
        return new ThrowNodeGenerator();
    }

    public ThrowNodeGenerator expression(ExpressionBaseNode throwValue) {
        this.throwValue = throwValue;
        return this;
    }

    public ThrowNode build() {
        return new ThrowNode(throwValue);
    }
}
