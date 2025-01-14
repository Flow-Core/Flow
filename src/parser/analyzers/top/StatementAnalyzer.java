package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.ExpressionAnalyzer;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.statements.ForStatementNode;
import parser.nodes.statements.IfStatementNode;

public class StatementAnalyzer implements TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(final Parser parser) {
        switch (parser.advance().type()) {
            case IF:
                parser.consume(TokenType.OPEN_PARENTHESES);

                ExpressionNode condition = ExpressionAnalyzer.parse(parser);

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
                    true
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
                    true
                );
            default:
                return null;
        }
    }
}
