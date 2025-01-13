package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;

public class ArgumentNode implements ASTNode {
    public String name;
    public ExpressionNode value;

    public ArgumentNode(String name, ExpressionNode value) {
        this.name = name;
        this.value = value;
    }
}