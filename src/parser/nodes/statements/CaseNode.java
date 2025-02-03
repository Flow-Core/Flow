package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionNode;

public class CaseNode implements StatementNode {
    public ExpressionNode value;
    public BlockNode body;

    public CaseNode(ExpressionNode value, BlockNode body) {
        this.value = value;
        this.body = body;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        value.accept(visitor, data);
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
