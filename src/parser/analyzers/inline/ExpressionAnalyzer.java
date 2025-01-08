package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.BinaryExpressionNode;
import parser.nodes.ExpressionNode;

import java.util.HashMap;

public class ExpressionAnalyzer {
    public static ExpressionNode parse(final Parser parser) {
        ExpressionNode currValue = PrimaryAnalyzer.parse(parser);
        if (currValue == null) return null;

        return parseRHS(parser, 0, currValue);
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

            parser.consume(TokenType.OPERATOR);

            ExpressionNode rhs = PrimaryAnalyzer.parse(parser);

            if (rhs == null) return null;

            Token nextOperator = parser.peek();
            int nextPrecedence = getPrecedence(nextOperator.value());

            if (opPrecedence < nextPrecedence) {
                rhs = parseRHS(parser, opPrecedence + 1, rhs);

                if (rhs == null) return null;
            }

            lhs = new BinaryExpressionNode(lhs, rhs, operator.value());
        }
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
        //</editor-fold>

        Integer value = precedenceValues.get(operator);

        if (value == null) return -1;
        return value;
    }
}
