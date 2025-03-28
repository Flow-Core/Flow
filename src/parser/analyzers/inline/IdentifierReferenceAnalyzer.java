package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.FlowType;
import parser.nodes.classes.TypeReferenceNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.generics.TypeArgument;
import parser.nodes.variable.VariableReferenceNode;

import java.util.ArrayList;
import java.util.List;

import static parser.analyzers.inline.FlowTypeAnalyzer.parseTypeArguments;

public class IdentifierReferenceAnalyzer {
    public ExpressionNode parse(final Parser parser) {
        final Token identifierToken = parser.peek(-1);
        final int line = identifierToken.line();

        if (parser.peek().value().equals("<")) {
            parser.checkpoint();
            try {
                List<TypeArgument> typeArguments = parseTypeArguments(parser);
                parser.clearCheckpoint();

                return new TypeReferenceNode(
                    new FlowType(
                        identifierToken.value(),
                        false,
                        false,
                        typeArguments
                    )
                );
            } catch (RuntimeException e) {
                parser.rollback();
            }
        }

        if (parser.check(TokenType.OPEN_PARENTHESES)) {
            parser.advance();
            final List<ArgumentNode> args = parseArguments(parser);
            parser.consume(TokenType.CLOSE_PARENTHESES);

            return new FunctionCallNode(identifierToken.value(), args);
        }

        return (ExpressionNode) ASTMetaDataStore.getInstance().addMetadata(new VariableReferenceNode(identifierToken.value()), line, parser.file);
    }

    public static List<ArgumentNode> parseArguments(final Parser parser) {
        final List<ArgumentNode> args = new ArrayList<>();

        while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
            // Parse named parameter
            if (parser.checkIgnoreNewLine(TokenType.IDENTIFIER) && parser.peek(1).type() == TokenType.EQUAL_OPERATOR) {
                final Token argumentName = parser.consume(TokenType.IDENTIFIER);
                parser.consume(TokenType.EQUAL_OPERATOR);
                final ExpressionNode value = ExpressionAnalyzer.parseExpression(parser);

                args.add((ArgumentNode) ASTMetaDataStore.getInstance().addMetadata(new ArgumentNode(argumentName.value(), new ExpressionBaseNode(value, parser.peek().line(), parser.file)), parser.peek().line(), parser.file));
            } else {
                final ExpressionNode value = ExpressionAnalyzer.parseExpression(parser);
                args.add((ArgumentNode) ASTMetaDataStore.getInstance().addMetadata(new ArgumentNode(null, new ExpressionBaseNode(value, parser.peek().line(), parser.file)), parser.peek().line(), parser.file));
            }

            if (!parser.checkIgnoreNewLine(TokenType.CLOSE_PARENTHESES)) {
                parser.consume(TokenType.COMMA);
            }
        }

        return args;
    }
}

