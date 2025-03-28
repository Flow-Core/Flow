package parser.nodes.literals;

import parser.nodes.expressions.ExpressionNode;

public interface LiteralNode extends ExpressionNode {
    String getClassName();
    Object getValue();
}
