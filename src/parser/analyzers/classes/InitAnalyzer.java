package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.components.BlockNode;

public class InitAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        final int line = parser.peek().line();
        TopAnalyzer.testFor(parser, TokenType.INIT);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(
            parser,
            AnalyzerDeclarations.getFunctionScope(),
            TokenType.CLOSE_BRACES
        );
        parser.consume(TokenType.CLOSE_BRACES);

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(block, line, parser.file),
            TerminationStatus.NO_TERMINATION
        );
    }
}