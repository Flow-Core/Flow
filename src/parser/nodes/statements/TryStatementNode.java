package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;

import java.util.List;

public class TryStatementNode implements ASTNode {
    public BlockNode tryBranch;
    public List<BlockNode> exceptionBranches;

    public TryStatementNode(BlockNode tryBranch, List<BlockNode> exceptionBranches) {
        this.tryBranch = tryBranch;
        this.exceptionBranches = exceptionBranches;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        tryBranch.accept(visitor);

        for (final BlockNode exception : exceptionBranches) {
            exception.accept(visitor);
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
