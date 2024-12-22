package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.ExpressionAnalyzer;
import parser.nodes.ExpressionNode;
import parser.nodes.FunctionCall;
import parser.nodes.VariableReference;

import java.util.ArrayList;
import java.util.List;

public class IdentifierReferenceAnalyzer implements TopAnalyzer {
    @Override
    public ExpressionNode parse(final Parser parser) {
        final Token identifierToken = parser.peek(-1);
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

