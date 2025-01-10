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

        return switch (token.type()) {
            case IPV6 -> new Ipv6LiteralNode(token.value());
            case IPV4 -> new Ipv4LiteralNode(token.value());
            case FLOAT -> new FloatLiteralNode(Float.parseFloat(token.value()));
            case DOUBLE -> new DoubleLiteralNode(Double.parseDouble(token.value()));
            case INT -> new IntegerLiteralNode(Integer.parseInt(token.value()));
            case STRING -> new StringLiteralNode(token.value());
            case BOOLEAN -> new BooleanLiteralNode(Boolean.parseBoolean(token.value()));
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