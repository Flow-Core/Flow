package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.top.BlockAnalyzer;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.FlowType;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.expressions.networking.StartNode;
import parser.nodes.functions.LambdaExpressionNode;
import parser.nodes.literals.*;
import parser.nodes.literals.ip.Ipv4LiteralNode;
import parser.nodes.literals.ip.Ipv6LiteralNode;

import java.util.ArrayList;
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
            case LONG -> new LongLiteralNode(Long.parseLong(token.value()));
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
                final FlowType type = FlowTypeAnalyzer.analyze(parser);

                parser.consume(TokenType.OPEN_PARENTHESES);
                final List<ArgumentNode> args = parseArguments(parser);
                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(new ObjectNode(type, args), line, parser.file);
            }
            case START -> {
                final FlowType serverType = FlowTypeAnalyzer.analyze(parser);

                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionBaseNode port = (ExpressionBaseNode) new ExpressionAnalyzer().parse(parser).node();
                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(new StartNode(serverType, port), line, parser.file);
            }
            case OPEN_PARENTHESES -> {
                ExpressionNode expr = ExpressionAnalyzer.parseExpression(parser);

                parser.consume(TokenType.CLOSE_PARENTHESES);

                yield (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(expr, line, parser.file);
            }
            case OPEN_BRACES -> {
                final List<ParameterNode> parameters = parseLambdaParameters(parser);

                final BlockNode block =  BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope(), TokenType.CLOSE_BRACES);
                parser.consume(TokenType.CLOSE_BRACES);

                FlowType returnType = new FlowType("Void", false, true);

                if (parser.check(TokenType.ARROW_OPERATOR)) {
                    parser.advance();
                    returnType = FlowTypeAnalyzer.analyze(parser);
                }

                yield (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(
                    new LambdaExpressionNode(
                        returnType,
                        new ArrayList<>(),
                        parameters,
                        new BodyNode(block)
                    ),
                    line,
                    parser.file
                );
            }
            default -> null;
        };
    }

    private static List<ParameterNode> parseLambdaParameters(Parser parser) {
        final List<ParameterNode> parameters = new ArrayList<>();

        while (!parser.check(TokenType.ARROW_OPERATOR)) {
            final Token name = parser.consume(TokenType.IDENTIFIER);
            parser.consume(TokenType.COLON_OPERATOR);
            final FlowType type = FlowTypeAnalyzer.analyze(parser);

            if (!parser.check(TokenType.ARROW_OPERATOR)) {
                parser.consume(TokenType.COMMA);
            }

            parameters.add((ParameterNode) ASTMetaDataStore.getInstance().addMetadata(
                new ParameterNode(
                    type,
                    name.value(),
                    null
                ),
                name.line(),
                parser.file
            ));
        }
        parser.advance();

        return parameters;
    }
}