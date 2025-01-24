package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public class SwitchStatementNode implements ASTNode {
    public ExpressionNode condition;
    public List<CaseNode> cases;
    public BlockNode defaultBlock;

    public SwitchStatementNode(ExpressionNode condition, List<CaseNode> cases, BlockNode defaultBlock) {
        this.condition = condition;
        this.cases = cases;
        this.defaultBlock = defaultBlock;
    }


    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        condition.accept(visitor, data);

        for (final CaseNode caseNode : cases) {
            caseNode.accept(visitor, data);
        }

        defaultBlock.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "SwitchStatementNode{" +
            "condition=" + condition +
            ", cases=" + cases +
            ", defaultBlock=" + defaultBlock +
            '}';
    }
}
