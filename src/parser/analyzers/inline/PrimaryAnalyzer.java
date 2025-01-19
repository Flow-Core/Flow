package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.*;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.literals.*;
import parser.nodes.literals.ip.Ipv4LiteralNode;
import parser.nodes.literals.ip.Ipv6LiteralNode;

import java.util.List;

import static parser.analyzers.inline.IdentifierReferenceAnalyzer.parseArguments;

public class PrimaryAnalyzer {
    public static ExpressionNode parse(final Parser parser) {
        final Token token = parser.advance();

        return switch (token.type()) {
            case NULL -> new NullLiteral();
            case IPV6 -> new Ipv6LiteralNode(token.value());
            case IPV4 -> new Ipv4LiteralNode(token.value());
            case FLOAT -> new FloatLiteralNode(Float.parseFloat(token.value()));
            case DOUBLE -> new DoubleLiteralNode(Double.parseDouble(token.value()));
            case INT -> new IntegerLiteralNode(Integer.parseInt(token.value()));
            case STRING -> new StringLiteralNode(token.value());
            case BOOLEAN -> new BooleanLiteralNode(Boolean.parseBoolean(token.value()));
            case IDENTIFIER -> new IdentifierReferenceAnalyzer().parse(parser);
            case NEW -> {
                final Token identifier = parser.consume(TokenType.IDENTIFIER);

                parser.consume(TokenType.OPEN_PARENTHESES);
                final List<ArgumentNode> args = parseArguments(parser);
                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield new ObjectNode(identifier.value(), args);
            }
            case OPEN_PARENTHESES -> {
                ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield expr;
            }
            default -> null;
        };
    }
}