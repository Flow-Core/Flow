package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;

public class IfStatementNode implements ASTNode {
    public ExpressionNode condition;
    public BlockNode trueBranch;
    public BlockNode falseBranch;

    public IfStatementNode(ExpressionNode condition, BlockNode trueBranch, BlockNode falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        condition.accept(visitor);

        trueBranch.accept(visitor);

        falseBranch.accept(visitor);
    }

    @Override
    public String toString() {
        return "IfStatementNode{" +
            "condition=" + condition +
            ", trueBranch=" + trueBranch +
            ", falseBranch=" + falseBranch +
            '}';
    }
}
