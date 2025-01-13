package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;

public class CaseNode implements ASTNode {
    public ExpressionNode value;
    public BlockNode body;

    public CaseNode(ExpressionNode value, BlockNode body) {
        this.value = value;
        this.body = body;
    }
}
