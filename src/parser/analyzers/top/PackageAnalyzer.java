package parser.analyzers.top;

import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.nodes.packages.PackageNode;

public class PackageAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.PACKAGE);
        final int line = parser.peek().line();

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(
                new PackageNode(parseModulePath(parser)),
                line,
                parser.file
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }

    public static String parseModulePath(final Parser parser) {
        final StringBuilder modulePath = new StringBuilder();

        modulePath.append(parser.consume(TokenType.IDENTIFIER).value());
        while (parser.peek().type() == TokenType.DOT_OPERATOR){
            modulePath.append(parser.consume(TokenType.DOT_OPERATOR).value());
            modulePath.append(parser.consume(TokenType.IDENTIFIER).value());
        }

        return modulePath.toString();
    }
}