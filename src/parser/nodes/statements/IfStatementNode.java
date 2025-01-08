package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;

public record IfStatementNode(
    ExpressionNode condition,
    BlockNode trueBranch,
    BlockNode falseBranch
) implements ASTNode {}
