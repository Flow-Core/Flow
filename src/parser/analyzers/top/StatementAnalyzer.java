package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.nodes.FlowType;
import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.literals.VoidLiteralNode;
import parser.nodes.statements.*;
import parser.nodes.variable.VariableAssignmentNode;

import java.util.ArrayList;
import java.util.List;

public class StatementAnalyzer extends TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(final Parser parser) {
        final Token currentToken = parser.advance();
        final int line = currentToken.line();
        switch (currentToken.type()) {
            case IF:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode ifCondition = ExpressionAnalyzer.parseExpression(parser);
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
                    ASTMetaDataStore.getInstance().addMetadata(
                        new IfStatementNode(
                            new ExpressionBaseNode(ifCondition, line, parser.file),
                            new BodyNode(trueBranch),
                            new BodyNode(falseBranch)
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

                final ExpressionNode forCondition = ExpressionAnalyzer.parseExpression(parser);

                parser.consume(TokenType.COMMA);

                BlockNode loopActionBlock = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getInlineScope(), TokenType.CLOSE_PARENTHESES);

                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode forBlock = getBlock(parser);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new ForStatementNode(
                            loopVariable,
                            new ExpressionBaseNode(forCondition, line, parser.file),
                            new BodyNode(loopActionBlock),
                            new BodyNode(forBlock)
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
                final ExpressionNode foreachCollection = ExpressionAnalyzer.parseExpression(parser);

                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode foreachBlock = getBlock(parser);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new ForeachStatementNode(
                            foreachVariable,
                            foreachCollection,
                            new BodyNode(foreachBlock)
                        ),
                        line,
                        parser.file
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case WHILE:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode whileCondition = ExpressionAnalyzer.parseExpression(parser);
                parser.consume(TokenType.CLOSE_PARENTHESES);

                final BlockNode whileBlock = getBlock(parser);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new WhileStatementNode(
                            new ExpressionBaseNode(whileCondition, line, parser.file),
                            new BodyNode(whileBlock)
                        ),
                        line,
                        parser.file
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case SWITCH:
                parser.consume(TokenType.OPEN_PARENTHESES);
                final ExpressionNode switchCondition = ExpressionAnalyzer.parseExpression(parser);
                parser.consume(TokenType.CLOSE_PARENTHESES);

                parser.consume(TokenType.OPEN_BRACES);
                final BlockNode switchBlock = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getSwitchScope(), TokenType.CLOSE_BRACES);
                parser.consume(TokenType.CLOSE_BRACES);

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new SwitchStatementNode(
                            new ExpressionBaseNode(switchCondition, line, parser.file),
                            switchBlock.children
                                .stream()
                                .filter(node -> node instanceof CaseNode)
                                .map(node -> (CaseNode) node)
                                .toList(),
                            switchBlock.children
                                .stream()
                                .filter(node -> node instanceof BodyNode)
                                .map(node -> (BodyNode) node)
                                .findFirst()
                                .orElse(null)
                        ),
                        line,
                        parser.file
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
                            new ParameterNode(
                                new FlowType(type, false, false),
                                parameterName, null
                            ),
                            new BodyNode(catchBlock)
                        )
                    );
                } while (parser.check(TokenType.CATCH));

                BlockNode finallyBlock = null;
                if (parser.check(TokenType.FINALLY)) {
                    parser.advance();

                    finallyBlock = getBlock(parser);
                }

                return new AnalyzerResult(
                    new TryStatementNode(
                        new BodyNode(tryBlock),
                        catchNodes,
                        new BodyNode(finallyBlock)
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case THROW:
                return new AnalyzerResult(
                    new ThrowNode(
                        new ExpressionBaseNode(
                            ExpressionAnalyzer.parseExpression(parser),
                            line,
                            parser.file
                        )
                    ),
                    parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                );
            case RETURN:
                if (parser.peek().isLineTerminator()) {
                    return new AnalyzerResult(
                        ASTMetaDataStore.getInstance().addMetadata(
                            new ReturnStatementNode(
                                (ExpressionBaseNode) ASTMetaDataStore.getInstance().addMetadata(
                                    new ExpressionBaseNode(
                                        new VoidLiteralNode(),
                                        line,
                                        parser.file
                                    ),
                                    line,
                                    parser.file
                                )
                            ),
                            line,
                            parser.file
                        ),
                        parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
                    );
                }

                return new AnalyzerResult(
                    ASTMetaDataStore.getInstance().addMetadata(
                        new ReturnStatementNode(
                            new ExpressionBaseNode(
                                ExpressionAnalyzer.parseExpression(parser),
                                line,
                                parser.file
                            )
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
