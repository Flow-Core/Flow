package parser.nodes.functions;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.List;
import java.util.Objects;

public class FunctionDeclarationNode implements ASTNode {
    public String name;
    public FlowType returnType;
    public List<String> modifiers;
    public List<ParameterNode> parameters;
    public List<TypeParameterNode> typeParameters;
    public BodyNode body;

    public FunctionDeclarationNode(
        String name,
        FlowType returnType,
        List<String> modifiers,
        List<ParameterNode> parameters,
        List<TypeParameterNode> typeParameters,
        BodyNode body
    ) {
        this.name = name;
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.parameters = parameters;
        this.typeParameters = typeParameters;
        this.body = body;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        for (final ParameterNode parameterNode : parameters) {
            parameterNode.accept(visitor, data);
        }

        for (final TypeParameterNode typeParameterNode : typeParameters) {
            typeParameterNode.accept(visitor, data);
        }

        if (body != null) {
            body.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionDeclarationNode that = (FunctionDeclarationNode) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(returnType, that.returnType)) return false;
        if (!Objects.equals(modifiers, that.modifiers)) return false;
        if (!Objects.equals(parameters, that.parameters)) return false;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (modifiers != null ? modifiers.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String modifier : modifiers) {
            sb.append(modifier).append(" ");
        }

        sb.append("func ").append(name);

        if (!typeParameters.isEmpty()) {
            sb.append("<");
        }
        for (TypeParameterNode typeParameterNode : typeParameters) {
            sb.append(typeParameterNode.name).append(": ").append(typeParameterNode.bound).append(", ");
        }
        if (!typeParameters.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
            sb.append(">");
        }

        sb.append("(");

        for (ParameterNode parameterNode : parameters) {
            sb.append(parameterNode).append(", ");
        }

        if (!parameters.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");

        sb.append(": ").append(returnType);

        return sb.toString();
    }
}
