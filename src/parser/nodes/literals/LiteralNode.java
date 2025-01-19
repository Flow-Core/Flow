package parser.nodes.literals;

import parser.nodes.ExpressionNode;

public interface LiteralNode extends ExpressionNode {
    String getClassName();
}
