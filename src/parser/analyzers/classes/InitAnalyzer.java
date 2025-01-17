package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.components.BlockNode;

public class InitAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.INIT);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(
            parser,
            AnalyzerDeclarations.getFunctionScope(),
            TokenType.CLOSE_BRACES
        );
        parser.consume(TokenType.CLOSE_BRACES);

        return new AnalyzerResult(
            block,
            TerminationStatus.NO_TERMINATION
        );
    }
}
