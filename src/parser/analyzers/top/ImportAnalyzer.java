package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.exceptions.PARSE_UnexpectedToken;
import parser.nodes.packages.ImportNode;

public class ImportAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.IMPORT);

        String moduleName = parser.consume(TokenType.IDENTIFIER).value();
        final StringBuilder modulePathBuilder = new StringBuilder(moduleName);
        while (parser.peek().type() == TokenType.DOT_OPERATOR){
            modulePathBuilder.append(parser.consume(TokenType.DOT_OPERATOR).value());

            if (parser.check(TokenType.BINARY_OPERATOR) && !parser.peek().value().equals("*")) {
                throw new PARSE_UnexpectedToken(parser.advance().value());
            }

            moduleName = parser.consume(TokenType.IDENTIFIER, TokenType.BINARY_OPERATOR).value();
            modulePathBuilder.append(moduleName);
        }

        final String modulePath = modulePathBuilder.toString();
        final boolean isWildcard = moduleName.equals("*");

        if (parser.check(TokenType.AS)) {
            parser.advance();

            final String alias = parser.consume(TokenType.IDENTIFIER).value();
            return new AnalyzerResult(
                new ImportNode(
                    modulePath,
                    alias,
                    isWildcard
                ),
                parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
            );
        }

        return new AnalyzerResult(
            new ImportNode(
                modulePath,
                moduleName,
                isWildcard
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
