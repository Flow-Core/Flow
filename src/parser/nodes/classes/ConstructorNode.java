package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
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

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        for (final ParameterNode parameter : parameters) {
            parameter.accept(visitor, data);
        }

        body.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "ConstructorNode{" +
            "accessModifier='" + accessModifier + '\'' +
            ", parameters=" + parameters +
            ", body=" + body +
            '}';
    }
}
