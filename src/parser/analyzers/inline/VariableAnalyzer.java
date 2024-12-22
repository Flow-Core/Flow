package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.ExpressionNode;
import parser.nodes.variable.VariableAssignment;
import parser.nodes.variable.VariableDeclaration;
import parser.nodes.variable.VariableReference;

public class VariableAnalyzer {
    public static VariableReference parseReference(final Parser parser) {
        final Token variable = parser.consume(TokenType.IDENTIFIER);

        return new VariableReference(variable.getValue());
    }

    public static VariableAssignment parseAssignment(final Parser parser) {
        final Token variable = parser.consume(TokenType.IDENTIFIER);

        parser.consume(TokenType.EQUAL_OPERATOR);

        final ExpressionNode expr = ExpressionAnalyzer.parse(parser);

        return new VariableAssignment(variable.getValue(), expr);
    }

    public static VariableDeclaration parseDeclaration(final Parser parser) {
        final Token modifier = parser.consume(TokenType.IDENTIFIER);
        final Token name = parser.consume(TokenType.IDENTIFIER);

        // Check for type specification
        if (parser.peek().getType() == TokenType.EQUAL_OPERATOR) {
            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = ExpressionAnalyzer.parse(parser);

            return new VariableDeclaration(modifier.getValue(), null, name.getValue(), expr);
        } else if (parser.peek().getType() == TokenType.COLON_OPERATOR) {
            parser.consume(TokenType.COLON_OPERATOR);
            final Token type = parser.consume(TokenType.IDENTIFIER);
            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = ExpressionAnalyzer.parse(parser);

            return new VariableDeclaration(modifier.getValue(), type.getValue(), name.getValue(), expr);
        }

        return null;
    }
}
