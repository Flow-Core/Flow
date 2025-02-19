package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.literals.*;
import parser.nodes.literals.ip.Ipv4LiteralNode;
import parser.nodes.literals.ip.Ipv6LiteralNode;

import java.util.List;

import static parser.analyzers.inline.IdentifierReferenceAnalyzer.parseArguments;

public class PrimaryAnalyzer {
    public static ExpressionNode parse(final Parser parser) {
        final Token token = parser.advance();
        final int line = token.line();

        return switch (token.type()) {
            case NULL -> new NullLiteral();
            case IPV6 -> new Ipv6LiteralNode(token.value());
            case IPV4 -> new Ipv4LiteralNode(token.value());
            case FLOAT -> new FloatLiteralNode(Float.parseFloat(token.value()));
            case DOUBLE -> new DoubleLiteralNode(Double.parseDouble(token.value()));
            case INT -> {
                try {
                    yield new IntegerLiteralNode(Integer.parseInt(token.value()));
                } catch (NumberFormatException e) {
                    yield new LongLiteralNode(Long.parseLong(token.value()));
                }
            }
            case STRING -> new StringLiteralNode(token.value());
            case CHAR -> new CharLiteralNode(token.value().charAt(0));
            case BOOLEAN -> new BooleanLiteralNode(Boolean.parseBoolean(token.value()));
            case IDENTIFIER -> new IdentifierReferenceAnalyzer().parse(parser);
            case NEW -> {
                final Token identifier = parser.consume(TokenType.IDENTIFIER);

                parser.consume(TokenType.OPEN_PARENTHESES);
                final List<ArgumentNode> args = parseArguments(parser);
                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(new ObjectNode(identifier.value(), args), line, parser.file);
            }
            case OPEN_PARENTHESES -> {
                ExpressionNode expr = ExpressionAnalyzer.parseExpression(parser);

                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(expr, line, parser.file);
            }
            default -> null;
        };
    }
}