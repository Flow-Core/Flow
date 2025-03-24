package parser.nodes.expressions;

import parser.nodes.FlowType;

import java.util.Objects;

public class IsExpressionNode implements ExpressionNode {
    public ExpressionNode base;
    public FlowType checkType;
    public boolean isNegated;

    public IsExpressionNode(ExpressionNode base, FlowType checkType, boolean isNegated) {
        this.base = base;
        this.checkType = checkType;
        this.isNegated = isNegated;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        IsExpressionNode that = (IsExpressionNode) o;
        return isNegated == that.isNegated && Objects.equals(base, that.base) && Objects.equals(checkType, that.checkType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(base);
        result = 31 * result + Objects.hashCode(checkType);
        result = 31 * result + Boolean.hashCode(isNegated);
        return result;
    }

    @Override
    public String toString() {
        return base + " is " + (isNegated ? "not " : "") + checkType;
    }
}
