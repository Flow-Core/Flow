package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;

import java.util.List;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TryStatementNode that = (TryStatementNode) o;

        if (!Objects.equals(tryBranch, that.tryBranch)) return false;
        return Objects.equals(exceptionBranches, that.exceptionBranches);
    }

    @Override
    public int hashCode() {
        int result = tryBranch != null ? tryBranch.hashCode() : 0;
        result = 31 * result + (exceptionBranches != null ? exceptionBranches.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TryStatementNode{" +
            "tryBranch=" + tryBranch +
            ", exceptionBranches=" + exceptionBranches +
            '}';
    }
}
