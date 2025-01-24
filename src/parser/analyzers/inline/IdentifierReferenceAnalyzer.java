package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.variable.VariableReferenceNode;

import java.util.ArrayList;
import java.util.List;

public class IdentifierReferenceAnalyzer {
    public ExpressionNode parse(final Parser parser) {
        final Token identifierToken = parser.peek(-1);

        if (parser.check(TokenType.OPEN_PARENTHESES)) {
            parser.advance();
            final List<ArgumentNode> args = parseArguments(parser);
            parser.consume(TokenType.CLOSE_PARENTHESES);

            return new FunctionCallNode(identifierToken.value(), args);
        }

        return new VariableReferenceNode(identifierToken.value());
    }

    public static List<ArgumentNode> parseArguments(final Parser parser) {
        final List<ArgumentNode> args = new ArrayList<>();

        while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
            if (parser.check(TokenType.NEW_LINE)) {
                parser.advance();
            }

            // Parse named parameter
            if (parser.peek().type() == TokenType.IDENTIFIER && parser.peek(1).type() == TokenType.EQUAL_OPERATOR) {
                final Token argumentName = parser.consume(TokenType.IDENTIFIER);
                parser.consume(TokenType.EQUAL_OPERATOR);
                final ExpressionNode value = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

                args.add(new ArgumentNode(argumentName.value(), value));
            } else {
                final ExpressionNode value = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
                args.add(new ArgumentNode(null, value));
            }

            if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                if (parser.check(TokenType.NEW_LINE)) {
                    parser.advance();
                } else {
                    parser.consume(TokenType.COMMA);
                }
            }
        }

        return args;
    }
}

