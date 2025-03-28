package parser.nodes.literals;

import parser.nodes.expressions.ExpressionNode;

public class NullLiteral implements ExpressionNode {
    @Override
    public String toString() {
        return "null";
    }
}
