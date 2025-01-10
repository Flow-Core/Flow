package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.ExpressionAnalyzer;
import parser.analyzers.inline.VariableAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.statements.ForStatementNode;
import parser.nodes.statements.IfStatementNode;
import parser.nodes.statements.WhileStatementNode;
import parser.nodes.variable.VariableAssignment;
import parser.nodes.variable.VariableDeclaration;

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
            case WHILE:
                parser.consume(TokenType.OPEN_PARENTHESES);

                ExpressionNode loopCondition = ExpressionAnalyzer.parse(parser);

                parser.consume(TokenType.CLOSE_PARENTHESES);

                parser.consume(TokenType.OPEN_BRACES);

                BlockNode loopBlock = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope());

                parser.consume(TokenType.CLOSE_BRACES);

                return new WhileStatementNode(
                        loopCondition,
                        loopBlock
                );
            case FOR:
                parser.consume(TokenType.OPEN_PARENTHESES);

                VariableDeclaration loopVariable = VariableAnalyzer.parseDeclaration(parser);

                parser.consume(TokenType.COMMA);

                ExpressionNode forCondition = ExpressionAnalyzer.parse(parser);

                parser.consume(TokenType.COMMA);

                VariableAssignment loopUpdate = VariableAnalyzer.parseAssignment(parser);

                parser.consume(TokenType.CLOSE_PARENTHESES);
                parser.consume(TokenType.OPEN_BRACES);

                BlockNode forBlock = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope());

                parser.consume(TokenType.CLOSE_BRACES);

                return new ForStatementNode(
                        loopVariable,
                        forCondition,
                        loopUpdate,
                        forBlock
                );
            default:
                return null;
        }
    }
}
