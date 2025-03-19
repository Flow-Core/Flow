package parser.analyzers.inline;

import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.FlowType;
import parser.nodes.generics.TypeArgument;
import semantic_analysis.visitors.ExpressionTraverse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static parser.analyzers.top.PackageAnalyzer.parseModulePath;

public class FlowTypeAnalyzer {
    public static FlowType analyze(Parser parser) {
        String name;
        boolean isNullable = false;
        List<TypeArgument> typeArguments = new ArrayList<>();

        if (parser.check(TokenType.OPEN_PARENTHESES)) {
            parser.advance();

            return parseLambdaType(parser);
        }

        name = parseModulePath(parser);

        if (parser.peek().value().equals("<")) {
            typeArguments = parseTypeArguments(parser);
        }

        if (parser.check(TokenType.NULLABLE)) {
            parser.advance();
            isNullable = true;
        }

        return new FlowType(name, isNullable, false, typeArguments);
    }

    public static List<TypeArgument> parseTypeArguments(Parser parser) {
        List<TypeArgument> typeArguments = new ArrayList<>();

        parser.advance();
        while (!parser.peek().value().equals(">")) {
            typeArguments.add(
                new TypeArgument(analyze(parser))
            );

            if (!parser.peek().value().equals(">")) {
                parser.consume(TokenType.COMMA);
            }
        }
        parser.advance();

        return typeArguments;
    }

    private static FlowType parseLambdaType(Parser parser) {
        final List<FlowType> parameters = new ArrayList<>();

        if (parser.check(TokenType.OPEN_PARENTHESES)) {
            parser.advance();
        }

        while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
            parameters.add(analyze(parser));

            if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                parser.consume(TokenType.COMMA);
            }
        }

        parser.advance();
        parser.consume(TokenType.ARROW_OPERATOR);

        final FlowType returnType = analyze(parser);
        boolean isNullable = false;

        if (parser.check(TokenType.CLOSE_PARENTHESES)) {
            parser.advance();

            if (parser.check(TokenType.NULLABLE)) {
                isNullable = true;
            }
        }

        final boolean hasReturnValue = !returnType.name.equals("Void");
        final List<TypeArgument> typeArguments = parameters
            .stream()
            .map(parameter -> new TypeArgument(parameter))
            .collect(Collectors.toCollection(ArrayList::new));

        if (hasReturnValue) {
            typeArguments.add(new TypeArgument(returnType));
        }

        return new FlowType(
            ExpressionTraverse.getLambdaInterfaceName(parameters.size(), hasReturnValue),
            isNullable,
            false,
            typeArguments
        );
    }
}