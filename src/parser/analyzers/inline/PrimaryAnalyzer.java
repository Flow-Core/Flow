package parser.analyzers.inline;

import lexer.token.Token;
import parser.Parser;
import parser.nodes.*;
import parser.nodes.literals.*;

public class PrimaryAnalyzer {
    public static ASTNode parse(final Parser parser) {
        final Token token = parser.advance();

        return switch (token.getType()) {
            case IPV6 -> new Ipv6Literal(token.getValue());
            case IPV4 -> new Ipv4Literal(token.getValue());
            case FLOAT -> new FloatLiteral(Float.parseFloat(token.getValue()));
            case DOUBLE -> new DoubleLiteral(Double.parseDouble(token.getValue()));
            case INT -> new IntegerLiteral(Integer.parseInt(token.getValue()));
            case STRING -> new StringLiteral(token.getValue());
            case BOOLEAN -> new BooleanLiteral(Boolean.parseBoolean(token.getValue()));
            case IDENTIFIER -> IdentifierReferenceAnalyzer.parse(parser, token);
            default -> null;
        };
    }
}