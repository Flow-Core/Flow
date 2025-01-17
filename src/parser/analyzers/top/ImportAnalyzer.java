package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.nodes.packages.ImportNode;

import java.util.Objects;

import static parser.analyzers.top.PackageAnalyzer.parseModulePath;

public class ImportAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.IMPORT);

        final String modulePath = parseModulePath(parser);
        final String module = parser.consume(TokenType.IDENTIFIER, TokenType.BINARY_OPERATOR).value();
        final boolean isWildcard = Objects.equals(module, "*");

        if (parser.check(TokenType.AS)) {
            parser.advance();

            final String alias = parser.consume(TokenType.IDENTIFIER).value();
            return new AnalyzerResult(
                new ImportNode(
                    modulePath + module,
                    alias,
                    isWildcard
                ),
                parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
            );
        }

        return new AnalyzerResult(
            new ImportNode(
                modulePath + module,
                null,
                isWildcard
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
