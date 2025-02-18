package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BodyNode;

import java.util.List;
import java.util.Objects;

public class TryStatementNode implements StatementNode {
    public BodyNode tryBranch;
    public List<CatchNode> exceptionBranches;
    public BodyNode finallyBranch;

    public TryStatementNode(BodyNode tryBranch, List<CatchNode> exceptionBranches, BodyNode finallyBranch) {
        this.tryBranch = tryBranch;
        this.exceptionBranches = exceptionBranches;
        this.finallyBranch = finallyBranch;
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
        if (o == null || getClass() != o.getClass()) return false;
        TryStatementNode that = (TryStatementNode) o;
        return Objects.equals(tryBranch, that.tryBranch) && Objects.equals(exceptionBranches, that.exceptionBranches) && Objects.equals(finallyBranch, that.finallyBranch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tryBranch, exceptionBranches, finallyBranch);
    }

    @Override
    public String toString() {
        return "TryStatementNode{" +
            "tryBranch=" + tryBranch +
            ", exceptionBranches=" + exceptionBranches +
            ", finallyBranch=" + finallyBranch +
            '}';
    }
}
