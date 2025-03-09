package parser.analyzers.inline;

import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.FlowType;
import parser.nodes.generics.TypeArgument;

import java.util.ArrayList;
import java.util.List;

import static parser.analyzers.top.PackageAnalyzer.parseModulePath;

public class FlowTypeAnalyzer {
    public static FlowType analyze(Parser parser) {
        String name;
        boolean isNullable = false;
        List<TypeArgument> typeArguments = new ArrayList<>();

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
}