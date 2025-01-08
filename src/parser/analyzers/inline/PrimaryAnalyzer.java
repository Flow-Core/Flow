package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.top.IdentifierReferenceAnalyzer;
import parser.nodes.*;
import parser.nodes.literals.*;

public class PrimaryAnalyzer {
    public static ExpressionNode parse(final Parser parser) {
        final Token token = parser.advance();

        return switch (token.getType()) {
            case IPV6 -> new Ipv6Literal(token.getValue());
            case IPV4 -> new Ipv4Literal(token.getValue());
            case FLOAT -> new FloatLiteralNode(Float.parseFloat(token.getValue()));
            case DOUBLE -> new DoubleLiteralNode(Double.parseDouble(token.getValue()));
            case INT -> new IntegerLiteral(Integer.parseInt(token.getValue()));
            case STRING -> new StringLiteral(token.getValue());
            case BOOLEAN -> new BooleanLiteralNode(Boolean.parseBoolean(token.getValue()));
            case IDENTIFIER -> new IdentifierReferenceAnalyzer().parse(parser);
            case OPEN_PARENTHESES -> {
                ExpressionNode expr = ExpressionAnalyzer.parse(parser);

                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield expr;
            }
            default -> null;
        };
    }
}