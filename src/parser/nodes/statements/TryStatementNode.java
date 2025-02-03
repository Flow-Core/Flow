package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;

import java.util.List;

public class TryStatementNode implements StatementNode {
    public BlockNode tryBranch;
    public List<CatchNode> exceptionBranches;

    public TryStatementNode(BlockNode tryBranch, List<CatchNode> exceptionBranches) {
        this.tryBranch = tryBranch;
        this.exceptionBranches = exceptionBranches;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        tryBranch.accept(visitor, data);

        for (final CatchNode exception : exceptionBranches) {
            exception.accept(visitor, data);
        }
    }

    @Override
    public String toString() {
        return "TryStatementNode{" +
            "tryBranch=" + tryBranch +
            ", exceptionBranches=" + exceptionBranches +
            '}';
    }
}
