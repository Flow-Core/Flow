package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.ExpressionAnalyzer;
import parser.nodes.ExpressionNode;
import parser.nodes.FunctionCallNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.variable.VariableReferenceNode;

import java.util.ArrayList;
import java.util.List;

public class IdentifierReferenceAnalyzer implements TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(final Parser parser) {
        final Token identifierToken = parser.peek(-1);
        if (parser.peek().type() == TokenType.OPEN_PARENTHESES) {
            final List<ArgumentNode> args = parseArguments(parser);

            return new AnalyzerResult(
                new FunctionCallNode(identifierToken.value(), args),
                parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON)
            );
        }

        return new AnalyzerResult(
            new VariableReferenceNode(identifierToken.value()),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON)
        );
    }

    public static List<ArgumentNode> parseArguments(final Parser parser) {
        final List<ArgumentNode> args = new ArrayList<>();

        parser.advance();
        while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
            // Parse named parameter
            if (parser.peek().type() == TokenType.IDENTIFIER && parser.peek(1).type() == TokenType.EQUAL_OPERATOR) {
                final Token argumentName = parser.consume(TokenType.IDENTIFIER);
                parser.consume(TokenType.EQUAL_OPERATOR);
                ExpressionNode value = ExpressionAnalyzer.parse(parser);

                args.add(new ArgumentNode(argumentName.value(), value));
            } else {
                ExpressionNode value = ExpressionAnalyzer.parse(parser);
                args.add(new ArgumentNode(null, value));
            }

            if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                parser.consume(TokenType.COMMA);
            }
        }
        parser.advance();

        return args;
    }
}

