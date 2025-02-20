package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.List;
import java.util.Objects;

public class SwitchStatementNode implements StatementNode {
    public ExpressionBaseNode condition;
    public List<CaseNode> cases;
    public BodyNode defaultBlock;

    public SwitchStatementNode(ExpressionBaseNode condition, List<CaseNode> cases, BodyNode defaultBlock) {
        this.condition = condition;
        this.cases = cases;
        this.defaultBlock = defaultBlock;
    }


    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        condition.accept(visitor, data);

        for (final CaseNode caseNode : cases) {
            caseNode.accept(visitor, data);
        }

        defaultBlock.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwitchStatementNode that = (SwitchStatementNode) o;

        if (!Objects.equals(condition, that.condition)) return false;
        if (!Objects.equals(cases, that.cases)) return false;
        return Objects.equals(defaultBlock, that.defaultBlock);
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + (cases != null ? cases.hashCode() : 0);
        result = 31 * result + (defaultBlock != null ? defaultBlock.hashCode() : 0);
        return result;
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
