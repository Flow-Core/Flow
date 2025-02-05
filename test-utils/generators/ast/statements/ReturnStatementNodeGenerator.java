package generators.ast.statements;

import parser.nodes.statements.ReturnStatementNode;
import parser.nodes.expressions.ExpressionBaseNode;

public class ReturnStatementNodeGenerator {
    private ExpressionBaseNode expression;

    public static ReturnStatementNodeGenerator builder() {
        return new ReturnStatementNodeGenerator();
    }

    public ReturnStatementNodeGenerator expression(ExpressionBaseNode expression) {
        this.expression = expression;
        return this;
    }

    public ReturnStatementNode build() {
        return new ReturnStatementNode(expression);
    }
}
