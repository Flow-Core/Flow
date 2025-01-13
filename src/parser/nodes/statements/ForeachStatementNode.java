package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;

public class ForeachStatementNode implements ASTNode {
    public ASTNode loopVariable;
    public ExpressionNode collection;
    public BlockNode body;

    public ForeachStatementNode(ASTNode loopVariable, ExpressionNode collection, BlockNode body) {
        this.loopVariable = loopVariable;
        this.collection = collection;
        this.body = body;
    }
}