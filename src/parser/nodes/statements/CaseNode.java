package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;

public class CaseNode implements ASTNode {
    public ExpressionNode value;
    public BlockNode body;

    public CaseNode(ExpressionNode value, BlockNode body) {
        this.value = value;
        this.body = body;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        body.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "CaseNode{" +
            "value=" + value +
            ", body=" + body +
            '}';
    }
}
