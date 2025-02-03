package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.statements.*;
import parser.nodes.variable.VariableAssignmentNode;

import java.util.ArrayList;
import java.util.List;

public class StatementAnalyzer extends TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(final Parser parser) {
        switch (parser.advance().type()) {
            case IF:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode ifCondition = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode trueBranch = getBlock(parser);

                if (parser.check(TokenType.NEW_LINE) && parser.peek(1).type() == TokenType.ELSE) {
                    parser.advance();
                }

                BlockNode falseBranch = null;
                if (parser.check(TokenType.ELSE)) {
                    parser.advance();
                    falseBranch = getBlock(parser);
                }

                return new AnalyzerResult(
                    new IfStatementNode(
                        ifCondition,
                        trueBranch,
                        falseBranch
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
                    new ForStatementNode(
                        loopVariable,
                        forCondition,
                        loopUpdate,
                        forBlock
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
                    new ForeachStatementNode(
                        foreachVariable,
                        foreachCollection,
                        foreachBlock
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case WHILE:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode whileCondition = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode whileBlock = getBlock(parser);

                return new AnalyzerResult(
                    new WhileStatementNode(
                        whileCondition, whileBlock
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
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case TRY:
                final BlockNode tryBlock = getBlock(parser);
                final List<CatchNode> catchNodes = new ArrayList<>();

                do {
                    parser.consume(TokenType.CATCH);

                    parser.consume(TokenType.OPEN_PARENTHESES);

                    final String parameterName = parser.consume(TokenType.IDENTIFIER).value();
                    parser.consume(TokenType.COLON_OPERATOR);
                    final String type = parser.consume(TokenType.IDENTIFIER).value();

                    parser.consume(TokenType.CLOSE_PARENTHESES);


                    final BlockNode catchBlock = getBlock(parser);

                    catchNodes.add(
                        new CatchNode(
                            new ParameterNode(type, false, parameterName, null),
                            catchBlock
                        )
                    );
                } while (parser.check(TokenType.CATCH));

                return new AnalyzerResult(
                    new TryStatementNode(
                        tryBlock,
                        catchNodes
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case THROW:
                return new AnalyzerResult(
                    new ThrowNode(
                        (ExpressionNode) new ExpressionAnalyzer().parse(parser).node()
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case RETURN:
                return new AnalyzerResult(
                    new ReturnStatementNode(
                        (ExpressionNode) new ExpressionAnalyzer().parse(parser).node()
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            default:
                return null;
        }
    }

    private BlockNode getBlock(final Parser parser) {
        final BlockNode block;

        if (parser.check(TokenType.NEW_LINE)) {
            parser.advance();
        }

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
