package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.IdentifierReferenceAnalyzer;
import parser.analyzers.inline.PrimaryAnalyzer;
import parser.nodes.BinaryExpressionNode;
import parser.nodes.ExpressionNode;

import java.util.HashMap;

public class ExpressionAnalyzer extends TopAnalyzer {
    public AnalyzerResult parse(final Parser parser) {
        ExpressionNode currValue = PrimaryAnalyzer.parse(parser);
        if (currValue == null) return null;

        return new AnalyzerResult(
            parseRHS(parser, 0, currValue),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }

    private static ExpressionNode parseRHS(
            Parser parser,
            int precedence,
            ExpressionNode lhs
    ) {
        while (true) {
            Token operator = parser.peek();
            int opPrecedence = getPrecedence(operator.value());

            if (opPrecedence < precedence)
                return lhs;

            ExpressionNode rhs;
            int nextPrecedence;

            if (parser.check(TokenType.DOT_OPERATOR)) {
                parser.advance();
                parser.advance();
                rhs = new IdentifierReferenceAnalyzer().parse(parser);
            } else {
                parser.consume(TokenType.OPERATOR);

                rhs = PrimaryAnalyzer.parse(parser);
            }

            if (rhs == null) return null;

            Token nextOperator = parser.peek();
            nextPrecedence = getPrecedence(nextOperator.value());

            if (opPrecedence < nextPrecedence) {
                rhs = parseRHS(parser, opPrecedence + 1, rhs);

                if (rhs == null) return null;
            }

            lhs = new BinaryExpressionNode(lhs, rhs, operator.value());
        }
    } // 123 * 13 + (13 / 123 + 123 * x.mashu).foo()

    private static int getPrecedence(String operator) {
        //<editor-fold desc="Precedence">
        final HashMap<String, Integer> precedenceValues = new HashMap<>();
        precedenceValues.put("&&", 10);
        precedenceValues.put("||", 10);
        precedenceValues.put("==", 20);
        precedenceValues.put("<", 20);
        precedenceValues.put(">", 20);
        precedenceValues.put("<=", 20);
        precedenceValues.put(">=", 20);
        precedenceValues.put("!=", 20);
        precedenceValues.put("+", 30);
        precedenceValues.put("-", 40);
        precedenceValues.put("*", 40);
        precedenceValues.put("/", 40);
        precedenceValues.put("%", 40);
        precedenceValues.put(".", 10000);
        //</editor-fold>

        Integer value = precedenceValues.get(operator);

        if (value == null) return -1;
        return value;
    }
}
