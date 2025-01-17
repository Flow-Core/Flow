package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.nodes.packages.PackageNode;

public class PackageAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.PACKAGE);

        return new AnalyzerResult(
            new PackageNode(
                parseModulePath(parser) + parser.consume(TokenType.IDENTIFIER).value()
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }

    public static String parseModulePath(final Parser parser) {
        final StringBuilder modulePath = new StringBuilder();
        while (parser.peek(1).type() == TokenType.DOT_OPERATOR && !parser.peek(2).isLineTerminator()) {
            modulePath.append(parser.consume(TokenType.IDENTIFIER).value());
            modulePath.append(parser.consume(TokenType.DOT_OPERATOR).value());
        }

        return modulePath.toString();
    }
}