package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.ExpressionNode;
import parser.nodes.variable.VariableReference;

public class IdentifierReferenceAnalyzer {
    public static ExpressionNode parse(final Parser parser, final Token currentToken) {
        if (parser.peek().getType() == TokenType.OPEN_PARENTHESES) {
            return null; // TODO: Function analyzer
        }

        return new VariableReference(currentToken.getValue());
    }
}
