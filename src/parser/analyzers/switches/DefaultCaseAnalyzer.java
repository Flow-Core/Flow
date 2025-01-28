package parser.analyzers.switches;

import lexer.token.TokenType;
import parser.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.components.BlockNode;

public class DefaultCaseAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.DEFAULT);
        final int line = parser.peek().line();

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope(), TokenType.CLOSE_BRACES);
        parser.consume(TokenType.CLOSE_BRACES);

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(block, line, parser.file),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
