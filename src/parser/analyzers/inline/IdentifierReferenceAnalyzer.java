package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.ExpressionNode;
import parser.nodes.FunctionCall;
import parser.nodes.VariableReference;

import java.util.ArrayList;
import java.util.List;

public class IdentifierReferenceAnalyzer {
    public static ExpressionNode parse(final Parser parser, final Token identifierToken) {
        if (parser.peek().getType() == TokenType.OPEN_PARENTHESES) {
            parser.advance();

            List<ExpressionNode> args = new ArrayList<>();

            while (true) {
                ExpressionNode arg = ExpressionAnalyzer.parse(parser);

                args.add(arg);

                if (parser.check(TokenType.CLOSE_PARENTHESES)) break;

                parser.consume(TokenType.COMMA);
            }

            parser.advance();

            return new FunctionCall(identifierToken.getValue(), args);
        }

        return new VariableReference(identifierToken.getValue());
    }
}
