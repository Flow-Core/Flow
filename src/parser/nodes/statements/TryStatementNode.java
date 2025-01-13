package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public class TryStatementNode implements ASTNode {
    public BlockNode tryBranch;
    public List<BlockNode> exceptionBranches;

    public TryStatementNode(BlockNode tryBranch, List<BlockNode> exceptionBranches) {
        this.tryBranch = tryBranch;
        this.exceptionBranches = exceptionBranches;
    }
}
