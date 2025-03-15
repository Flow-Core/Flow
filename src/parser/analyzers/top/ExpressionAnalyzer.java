package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.IdentifierReferenceAnalyzer;
import parser.analyzers.inline.PrimaryAnalyzer;
import parser.nodes.expressions.*;

import java.util.HashMap;

public class ExpressionAnalyzer extends TopAnalyzer {
    public AnalyzerResult parse(final Parser parser) {
        final int line = parser.peek().line();
        ExpressionNode currValue = (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(parseValue(parser), line, parser.file);
        if (currValue == null) return null;

        return new AnalyzerResult(
            new ExpressionBaseNode(parseRHS(parser, 0, currValue), line, parser.file),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }

    public static ExpressionNode parseExpression(final Parser parser) {
        ExpressionNode currValue = parseValue(parser);
        if (currValue == null) return null;

        return parseRHS(parser, 0, currValue);
    }

    private static ExpressionNode parseRHS(
            Parser parser,
            int precedence,
            ExpressionNode lhs
    ) {
        while (true) {
            final Token operator = parser.peek();
            final int opPrecedence = getPrecedence(operator.value());
            String operatorSign = operator.value();

            if (opPrecedence < precedence)
                return lhs;

            ExpressionNode rhs;
            int nextPrecedence;

            if (parser.check(TokenType.DOT_OPERATOR, TokenType.SAFE_CALL)) {
                parser.advance();
                parser.advance(); // IdentifierReferenceAnalyzer uses peek(-1)
                rhs = new IdentifierReferenceAnalyzer().parse(parser);
            } else if (parser.check(TokenType.OPEN_BRACKETS)) {
                rhs = parseBrackets(parser);
                operatorSign = "[]";
            } else {
                parser.consume(TokenType.BINARY_OPERATOR, TokenType.POLARITY_OPERATOR, TokenType.COLON_OPERATOR);

                rhs = parseValue(parser);
            }

            if (rhs == null) return null;

            Token nextOperator = parser.peek();
            nextPrecedence = getPrecedence(nextOperator.value());

            if (opPrecedence < nextPrecedence) {
                rhs = parseRHS(parser, opPrecedence + 1, rhs);

                if (rhs == null) return null;
            }

            lhs = new BinaryExpressionNode(lhs, rhs, operatorSign);
        }
    }

    private static ExpressionNode parseValue(Parser parser) {
        Token prefix = null, postfix = null;

        if (parser.check(TokenType.UNARY_OPERATOR, TokenType.POLARITY_OPERATOR))
            prefix = parser.advance();

        ExpressionNode value = PrimaryAnalyzer.parse(parser);

        if (parser.check(TokenType.UNARY_OPERATOR))
            postfix = parser.advance();

        if (postfix != null)
            value = new UnaryOperatorNode(value, postfix.value(), true);

        if (prefix != null)
            value = new UnaryOperatorNode(value, prefix.value(), false);

        return value;
    }

    private static ExpressionNode parseBrackets(Parser parser) {
        parser.consume(TokenType.OPEN_BRACKETS);

        ExpressionNode subscript = parseExpression(parser);

        parser.consume(TokenType.CLOSE_BRACKETS);

        return subscript;
    }

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
        precedenceValues.put("~", 50);
        precedenceValues.put("[", 100);
        precedenceValues.put(":", 1000);
        precedenceValues.put(".", 10000);
        precedenceValues.put("?.", 10000);
        //</editor-fold>

        Integer value = precedenceValues.get(operator);

        if (value == null) return -1;
        return value;
    }
}
