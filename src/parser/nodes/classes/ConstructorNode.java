package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;

import java.util.List;
import java.util.Objects;

public class ConstructorNode implements ASTNode {
    public String accessModifier;
    public List<ParameterNode> parameters;
    public BodyNode body;

    public ConstructorNode(String accessModifier, List<ParameterNode> parameters, BodyNode body) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstructorNode that = (ConstructorNode) o;

        if (!Objects.equals(accessModifier, that.accessModifier))
            return false;
        if (!Objects.equals(parameters, that.parameters)) return false;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = accessModifier != null ? accessModifier.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
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
