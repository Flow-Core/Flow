package parser.nodes.classes;

import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class CastNode implements ExpressionNode {
    public ExpressionNode base;
    public FlowType castType;

    public CastNode(ExpressionNode base, FlowType castType) {
        this.base = base;
        this.castType = castType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CastNode castNode = (CastNode) o;
        return Objects.equals(base, castNode.base) && Objects.equals(castType, castNode.castType);
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