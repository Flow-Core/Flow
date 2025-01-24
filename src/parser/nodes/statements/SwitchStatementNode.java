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
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        condition.accept(visitor);

        for (final CaseNode caseNode : cases) {
            caseNode.accept(visitor);
        }

        defaultBlock.accept(visitor);
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
