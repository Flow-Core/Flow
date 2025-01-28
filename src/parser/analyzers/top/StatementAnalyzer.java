package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.statements.*;
import parser.nodes.variable.VariableAssignmentNode;

public class StatementAnalyzer extends TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(final Parser parser) {
        final Token currentToken = parser.advance();
        final int line = currentToken.line();
        switch (currentToken.type()) {
            case IF:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode ifCondition = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode trueBranch = getBlock(parser);

                if (parser.check(TokenType.NEW_LINE)) {
                    parser.advance();
                }

                BlockNode falseBranch = null;
                if (parser.check(TokenType.ELSE)) {
                    parser.advance();
                    falseBranch = getBlock(parser);
                }

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new IfStatementNode(
                            ifCondition,
                            trueBranch,
                            falseBranch
                        ),
                        line,
                        parser.file
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case FOR:
                parser.consume(TokenType.OPEN_PARENTHESES);

                final VariableAssignmentNode loopVariable = (VariableAssignmentNode) new VariableAssignmentAnalyzer(false).parse(parser).node();

                parser.consume(TokenType.COMMA);

                final ExpressionNode forCondition = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

                parser.consume(TokenType.COMMA);

                final VariableAssignmentNode loopUpdate = (VariableAssignmentNode) new VariableAssignmentAnalyzer(true).parse(parser).node();

                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode forBlock = getBlock(parser);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new ForStatementNode(
                            loopVariable,
                            forCondition,
                            loopUpdate,
                            forBlock
                        ),
                        line,
                        parser.file
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case FOREACH:
                parser.consume(TokenType.OPEN_PARENTHESES);

                final String foreachVariable = parser.consume(TokenType.IDENTIFIER).value();
                parser.consume(TokenType.IN);
                final ExpressionNode foreachCollection = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode foreachBlock = getBlock(parser);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new ForeachStatementNode(
                            foreachVariable,
                            foreachCollection,
                            foreachBlock
                        ),
                        line,
                        parser.file
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case WHILE:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode whileCondition = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode whileBlock = getBlock(parser);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new WhileStatementNode(
                            whileCondition, whileBlock
                        ),
                        line,
                        parser.file
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case SWITCH:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode switchCondition = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
                parser.consume(TokenType.CLOSE_PARENTHESES);

                parser.consume(TokenType.OPEN_BRACES);
                final BlockNode switchBlock = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getSwitchScope(), TokenType.CLOSE_BRACES);
                parser.consume(TokenType.CLOSE_BRACES);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new SwitchStatementNode(
                            switchCondition,
                            switchBlock.children
                                .stream()
                                .filter(node -> node instanceof CaseNode)
                                .map(node -> (CaseNode) node)
                                .toList(),
                            switchBlock.children
                                .stream()
                                .filter(node -> node instanceof BlockNode)
                                .map(node -> (BlockNode) node)
                                .findFirst()
                                .orElse(null)
                        ),
                        line,
                        parser.file
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            default:
                return null;
        }
    }

    private BlockNode getBlock(final Parser parser) {
        final BlockNode block;
        if (parser.check(TokenType.OPEN_BRACES)) {
            parser.advance();
            block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope(), TokenType.CLOSE_BRACES);
            parser.consume(TokenType.CLOSE_BRACES);
        } else {
            block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getStatementScope());
        }

        return block;
    }
}
