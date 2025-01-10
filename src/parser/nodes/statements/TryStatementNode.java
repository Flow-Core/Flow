package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public record TryStatementNode(
    BlockNode tryBranch,
    List<BlockNode> exceptionBranches
) implements ASTNode {}
