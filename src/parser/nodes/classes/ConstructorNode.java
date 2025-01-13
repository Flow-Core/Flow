package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public class ConstructorNode implements ASTNode {
    public String accessModifier;
    public List<ParameterNode> parameters;
    public BlockNode body;

    public ConstructorNode(String accessModifier, List<ParameterNode> parameters, BlockNode body) {
        this.accessModifier = accessModifier;
        this.parameters = parameters;
        this.body = body;
    }
}
