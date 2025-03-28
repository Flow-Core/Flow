package parser.analyzers.switches;

import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.statements.CaseNode;

public class CaseAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.CASE);
        final int line = parser.peek().line();

        parser.consume(TokenType.OPEN_PARENTHESES);
        final ExpressionNode expression = ExpressionAnalyzer.parseExpression(parser);
        parser.consume(TokenType.CLOSE_PARENTHESES);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope(), TokenType.CLOSE_BRACES);
        parser.consume(TokenType.CLOSE_BRACES);

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(
                new CaseNode(
                    new ExpressionBaseNode(expression, line, parser.file),
                    new BodyNode(block)
                ),
                line,
                parser.file
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}

