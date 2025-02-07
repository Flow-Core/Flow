package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.variable.VariableReferenceNode;

import java.util.ArrayList;
import java.util.List;

public class IdentifierReferenceAnalyzer {
    public ExpressionNode parse(final Parser parser) {
        final Token identifierToken = parser.peek(-1);
        final int line = identifierToken.line();

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
            if (parser.check(TokenType.NEW_LINE)) {
                parser.advance();
            }

            // Parse named parameter
            if (parser.peek().type() == TokenType.IDENTIFIER && parser.peek(1).type() == TokenType.EQUAL_OPERATOR) {
                final Token argumentName = parser.consume(TokenType.IDENTIFIER);
                parser.consume(TokenType.EQUAL_OPERATOR);
                final ExpressionNode value = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

                args.add((ArgumentNode) ASTMetaDataStore.getInstance().addMetadata(new ArgumentNode(argumentName.value(), new ExpressionBaseNode(value, parser.peek().line(), parser.file)), parser.peek().line(), parser.file));
            } else {
                final ExpressionNode value = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
                args.add((ArgumentNode) ASTMetaDataStore.getInstance().addMetadata(new ArgumentNode(null, new ExpressionBaseNode(value, parser.peek().line(), parser.file)), parser.peek().line(), parser.file));
            }

            if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                if (parser.check(TokenType.NEW_LINE)) {
                    parser.advance();
                } else {
                    parser.consume(TokenType.COMMA);
                }
            }
        }

        return args;
    }
}

