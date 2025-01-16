package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.nodes.ExpressionNode;
import parser.nodes.variable.VariableAssignmentNode;

public class VariableAssignmentAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(Parser parser) {
        final Token variable = TopAnalyzer.testFor(parser, TokenType.IDENTIFIER);
        final String operator = TopAnalyzer.testFor(parser, TokenType.EQUAL_OPERATOR, TokenType.ASSIGNMENT_OPERATOR).value();
        final ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

        return new AnalyzerResult(
            new VariableAssignmentNode(variable.value(), operator, expr),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
