package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public class TryStatementNode implements ASTNode {
    private final BlockNode tryBranch;
    private final List<BlockNode> exceptionBranches;

    public TryStatementNode(final BlockNode tryBranch,
                            final List<BlockNode> exceptionBranches) {
        this.tryBranch = tryBranch;
        this.exceptionBranches = exceptionBranches;
    }

    public BlockNode getTryBranch() {
        return tryBranch;
    }

    public List<BlockNode> getExceptionBranches() {
        return exceptionBranches;
    }
}
