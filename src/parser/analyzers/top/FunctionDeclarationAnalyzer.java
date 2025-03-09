package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.analyzers.inline.FlowTypeAnalyzer;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.nodes.FlowType;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.components.BlockNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.ArrayList;
import java.util.List;

import static parser.analyzers.classes.ClassAnalyzer.parseTypeParameters;

public class FunctionDeclarationAnalyzer extends TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(final Parser parser) {
        final FunctionDeclarationNode functionDeclaration = parseFunctionSignature(parser);
        final int line = parser.peek().line();

        if (parser.check(TokenType.OPEN_BRACES)) {
            parser.advance();

            BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getFunctionScope(), TokenType.CLOSE_BRACES);

            parser.consume(TokenType.CLOSE_BRACES);

            functionDeclaration.body = new BodyNode(block);
        }

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(functionDeclaration, line, parser.file),
            TerminationStatus.NO_TERMINATION
        );
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

        TopAnalyzer.testFor(parser, TokenType.FUNC);

        Token funcName = parser.consume(TokenType.IDENTIFIER);

        final List<TypeParameterNode> typeParameters = parseTypeParameters(parser);

        List<ParameterNode> parameters = parseParameters(parser);

        FlowType returnType = new FlowType("Void", false, true);

        if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.advance();
            returnType = new FlowType(parser.consume(TokenType.IDENTIFIER).value(), false, false);

            if (parser.check(TokenType.NULLABLE)) {
                parser.advance();
                returnType.isNullable = true;
            }
        }

        return new FunctionDeclarationNode(
            funcName.value(),
            returnType,
            modifiers,
            parameters,
            typeParameters,
            null
        );
    }

    public static List<ParameterNode> parseParameters(Parser parser) {
        parser.consume(TokenType.OPEN_PARENTHESES);

        List<ParameterNode> parameters = new ArrayList<>();
        while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
            String name = parser.consume(TokenType.IDENTIFIER).value();
            parser.consume(TokenType.COLON_OPERATOR);

            final FlowType type = FlowTypeAnalyzer.analyze(parser);

            int line = parser.peek().line();

            ExpressionNode defaultValue = null;
            if (parser.peek().type() == TokenType.EQUAL_OPERATOR) {
                parser.advance();
                defaultValue = ExpressionAnalyzer.parseExpression(parser);
            }

            ParameterNode arg = (ParameterNode) ASTMetaDataStore.getInstance().addMetadata(new ParameterNode(
                type,
                name,
                new ExpressionBaseNode(defaultValue, line, parser.file)
            ), line, parser.file);

            parameters.add(arg);

            if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                parser.consume(TokenType.COMMA);
            }
        }
        parser.advance();

        return parameters;
    }
}
