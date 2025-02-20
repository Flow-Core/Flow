package generators.ast.statements;

import parser.nodes.FlowType;
import parser.nodes.statements.ReturnStatementNode;
import parser.nodes.expressions.ExpressionBaseNode;

public class ReturnStatementNodeGenerator {
    private ExpressionBaseNode expression;
    private FlowType returnType;

    public static ReturnStatementNodeGenerator builder() {
        return new ReturnStatementNodeGenerator();
    }

    public ReturnStatementNodeGenerator expression(ExpressionBaseNode expression) {
        this.expression = expression;
        return this;
    }

    public ReturnStatementNodeGenerator returnType(FlowType returnType) {
        this.returnType = returnType;
        return this;
    }

    public ReturnStatementNode build() {
        return new ReturnStatementNode(expression, returnType);
    }
}
