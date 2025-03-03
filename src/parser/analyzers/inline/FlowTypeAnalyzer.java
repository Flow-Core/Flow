package parser.analyzers.inline;

import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.FlowType;

import static parser.analyzers.top.PackageAnalyzer.parseModulePath;

public class FlowTypeAnalyzer {
    public static FlowType analyze(Parser parser) {
        String name;
        boolean isNullable = false;

        parser.advance();
        name = parseModulePath(parser);

        if (parser.check(TokenType.NULLABLE)) {
            parser.advance();
            isNullable = true;
        }

        return new FlowType(name, isNullable, false);
    }
}
