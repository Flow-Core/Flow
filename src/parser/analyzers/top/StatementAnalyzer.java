package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.statements.ForStatementNode;
import parser.nodes.statements.IfStatementNode;

public class StatementAnalyzer extends TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(final Parser parser) {
        switch (parser.advance().type()) {
            case IF:
                parser.consume(TokenType.OPEN_PARENTHESES);

                ExpressionNode condition = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

                parser.consume(TokenType.CLOSE_PARENTHESES);

                parser.consume(TokenType.OPEN_BRACES);

                BlockNode trueBranch = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope(), TokenType.CLOSE_BRACES);

                parser.consume(TokenType.CLOSE_BRACES);

                BlockNode falseBranch = null;

                if (parser.check(TokenType.ELSE)) {
                    parser.advance();
                    parser.consume(TokenType.OPEN_BRACES);

                    falseBranch = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope(), TokenType.CLOSE_BRACES);
                }

                return new AnalyzerResult(
                    new IfStatementNode(
                        condition,
                        trueBranch,
                        falseBranch
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case FOR:
                parser.consume(TokenType.OPEN_PARENTHESES);

                return new AnalyzerResult(
                    new ForStatementNode(
                        null,
                        null,
                        null,
                        null
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            default:
                return null;
        }
    }
}
