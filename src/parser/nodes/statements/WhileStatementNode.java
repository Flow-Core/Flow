package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;

public record WhileStatementNode(
    ExpressionNode condition,
    BlockNode loopBlock
) implements ASTNode {}
