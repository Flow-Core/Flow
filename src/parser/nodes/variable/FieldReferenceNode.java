package parser.nodes.variable;

import parser.nodes.expressions.ExpressionNode;

public class FieldReferenceNode implements ExpressionNode {
    public String holderType;
    public String name;
    public ExpressionNode holder;

    public FieldReferenceNode(
        String holderType,
        String name,
        ExpressionNode holder
    ) {
        this.holderType = holderType;
        this.name = name;
        this.holder = holder;
    }
}
