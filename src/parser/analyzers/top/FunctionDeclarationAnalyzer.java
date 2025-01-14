package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.nodes.ExpressionNode;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclarationAnalyzer implements TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(Parser parser) {
        final FunctionDeclarationNode functionDeclaration = parseFunctionSignature(parser);

        parser.consume(TokenType.OPEN_BRACES);

        BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getFunctionScope(), TokenType.CLOSE_BRACES);

        parser.consume(TokenType.CLOSE_BRACES);

        functionDeclaration.block = block;
        return new AnalyzerResult(functionDeclaration, TerminationStatus.NO_TERMINATION);
    }

    public static List<String> parseModifiers(final Parser parser) {
        final List<String> modifiers = new ArrayList<>();
        while (parser.check(TokenType.MODIFIER)) {
            modifiers.add(parser.advance().value());
        }

        return modifiers;
    }

    public static FunctionDeclarationNode parseFunctionSignature(final Parser parser) {
        final List<String> modifiers = parseModifiers(parser);

        parser.consume(TokenType.FUNC);

        Token funcName = parser.consume(TokenType.IDENTIFIER);

        parser.consume(TokenType.OPEN_PARENTHESES);

        List<ParameterNode> parameters = new ArrayList<>();

        while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
            String name = parser.consume(TokenType.IDENTIFIER).value();
            parser.consume(TokenType.COLON_OPERATOR);
            String type = parser.consume(TokenType.IDENTIFIER).value();

            ExpressionNode defaultValue = null;
            if (parser.peek().type() == TokenType.EQUAL_OPERATOR) {
                parser.advance();
                defaultValue = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
            }

            ParameterNode arg = new ParameterNode(type, name, defaultValue);

            parameters.add(arg);

            if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                parser.consume(TokenType.COMMA);
            }
        }
        parser.advance();

        String returnType = "Void";

        if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.advance();
            returnType = parser.consume(TokenType.IDENTIFIER).value();
        }

        return new FunctionDeclarationNode(
            funcName.value(),
            returnType,
            modifiers,
            parameters,
            null
        );
    }
}
