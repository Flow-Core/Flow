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
    public ExpressionNode parse(final Parser parser) {
        final Token identifierToken = parser.peek(-1);
        if (parser.peek().type() == TokenType.OPEN_PARENTHESES) {
            parser.advance();

            List<ArgumentNode> args = new ArrayList<>();
            while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                args.add(parseArgument(parser));

                if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                    parser.consume(TokenType.COMMA);
                }
            }

            parser.advance();
            return new FunctionCallNode(identifierToken.value(), args);
        }

        return new VariableReferenceNode(identifierToken.value());
    }

    private ArgumentNode parseArgument(Parser parser) {
        if (parser.peek().type() == TokenType.IDENTIFIER && parser.peek(1).type() == TokenType.EQUAL_OPERATOR) {
            final Token argumentName = parser.consume(TokenType.IDENTIFIER);
            parser.consume(TokenType.EQUAL_OPERATOR);
            ExpressionNode value = ExpressionAnalyzer.parse(parser);

            return new ArgumentNode(argumentName.value(), value);
        }

        ExpressionNode value = ExpressionAnalyzer.parse(parser);
        return new ArgumentNode(null, value);
    }
}

