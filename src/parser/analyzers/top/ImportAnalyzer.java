package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.nodes.packages.ImportNode;

public class ImportAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.IMPORT);

        final StringBuilder modulePath = new StringBuilder();
        while (!parser.peek().isLineTerminator()) {
            modulePath.append(parser.consume(TokenType.IDENTIFIER).value());
            modulePath.append(parser.consume(TokenType.DOT_OPERATOR).value());
        }

        final String module = parser.consume(TokenType.IDENTIFIER, TokenType.OPERATOR).value();

        if (parser.check(TokenType.AS)) {
            parser.advance();

            final String alias = parser.consume(TokenType.IDENTIFIER).value();
            return new AnalyzerResult(
                new ImportNode(
                    modulePath.toString(),
                    module,
                    alias
                ),
                parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
            );
        }

        return new AnalyzerResult(
            new ImportNode(
                modulePath.toString(),
                module,
                null
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
