package parser.nodes.expressions;

import parser.nodes.FlowType;

import java.util.Objects;

public class CastExpressionNode implements ExpressionNode {
    public ExpressionNode base;
    public FlowType castType;

    public CastExpressionNode(ExpressionNode base, FlowType castType) {
        this.base = base;
        this.castType = castType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CastExpressionNode castExpressionNode = (CastExpressionNode) o;
        return Objects.equals(base, castExpressionNode.base) && Objects.equals(castType, castExpressionNode.castType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(base);
        result = 31 * result + Objects.hashCode(castType);
        return result;
    }

    @Override
    public String toString() {
        return base + " as " + castType;
    }
}