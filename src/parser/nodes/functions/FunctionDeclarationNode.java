package parser.nodes.functions;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;

import java.util.List;
import java.util.Objects;

public class FunctionDeclarationNode implements ASTNode {
    public String name;
    public FlowType returnType;
    public List<String> modifiers;
    public List<ParameterNode> parameters;
    public BodyNode block;

    public FunctionDeclarationNode(String name, FlowType returnType, List<String> modifiers, List<ParameterNode> parameters, BodyNode block) {
        this.name = name;
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.parameters = parameters;
        this.block = block;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        if (block != null) {
            block.accept(visitor, data);
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
        return Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (modifiers != null ? modifiers.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionDeclarationNode{" +
            "name='" + name + '\'' +
            ", returnType=" + returnType +
            ", modifiers=" + modifiers +
            ", parameters=" + parameters +
            ", block=" + block +
            '}';
    }
}
