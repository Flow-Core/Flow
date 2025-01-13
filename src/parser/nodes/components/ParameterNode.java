package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;

public class ParameterNode implements ASTNode {
    public String type;
    public String name;
    public ExpressionNode defaultValue;

    public ParameterNode(String type, String name, ExpressionNode defaultValue) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }
}
