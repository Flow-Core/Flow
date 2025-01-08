package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.ExpressionAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.statements.ForStatementNode;
import parser.nodes.statements.IfStatementNode;

public class StatementAnalyzer implements TopAnalyzer {
    @Override
    public ASTNode parse(final Parser parser) {
        switch (parser.advance().type()) {
            case IF:
                parser.consume(TokenType.OPEN_PARENTHESES);

                ExpressionNode condition = ExpressionAnalyzer.parse(parser);

                parser.consume(TokenType.CLOSE_PARENTHESES);

                parser.consume(TokenType.OPEN_BRACES);

                BlockNode trueBranch = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope());

                parser.consume(TokenType.CLOSE_BRACES);

                BlockNode falseBranch = null;

                if (parser.check(TokenType.ELSE)) {
                    parser.advance();
                    parser.consume(TokenType.OPEN_BRACES);

                    falseBranch = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope());
                }

                return new IfStatementNode(
                        condition,
                        trueBranch,
                        falseBranch
                );
            case FOR:
                parser.consume(TokenType.OPEN_PARENTHESES);


                return new ForStatementNode(
                        null, null, null, null
                );
            default:
                return null;
        }
    }
}
