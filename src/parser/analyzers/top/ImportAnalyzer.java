package parser.analyzers.top;

import lexer.token.TokenType;
import logger.LoggerFacade;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.nodes.packages.ImportNode;

public class ImportAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.IMPORT);
        final int line = parser.peek().line();

        String moduleName = parser.consume(TokenType.IDENTIFIER).value();
        final StringBuilder modulePathBuilder = new StringBuilder(moduleName);
        while (parser.peek().type() == TokenType.DOT_OPERATOR){
            modulePathBuilder.append(parser.consume(TokenType.DOT_OPERATOR).value());

            if (parser.check(TokenType.BINARY_OPERATOR) && !parser.peek().value().equals("*")) {
                throw LoggerFacade.getLogger().panic("Unexpected token: '" + parser.advance().value() + "'", parser.peek().line(), parser.file);
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
                ASTMetaDataStore.getInstance().addMetadata(
                    new ImportNode(
                        modulePath,
                        alias,
                        isWildcard
                    ),
                    line,
                    parser.file
                ),
                parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
            );
        }

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(
                new ImportNode(
                    modulePath,
                    moduleName,
                    isWildcard
                ),
                line,
                parser.file
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
