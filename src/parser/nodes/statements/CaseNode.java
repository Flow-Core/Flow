package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;

import java.util.Objects;

public class CaseNode implements StatementNode {
    public ExpressionBaseNode value;
    public BodyNode body;

    public CaseNode(ExpressionBaseNode value, BodyNode body) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaseNode caseNode = (CaseNode) o;

        if (!Objects.equals(value, caseNode.value)) return false;
        return Objects.equals(body, caseNode.body);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CaseNode{" +
            "value=" + value +
            ", body=" + body +
            '}';
    }
}
