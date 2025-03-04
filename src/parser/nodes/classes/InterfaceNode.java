package parser.nodes.classes;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.List;
import java.util.Objects;

public class InterfaceNode extends TypeDeclarationNode {
    public List<String> modifiers;
    public BlockNode block;

    public InterfaceNode(
        String name,
        List<String> modifiers,
        List<TypeParameterNode> typeParameters,
        List<BaseInterfaceNode> implementedInterfaces,
        List<FunctionDeclarationNode> methods,
        BlockNode block
    ) {
        this.name = name;
        this.typeParameters = typeParameters;
        this.modifiers = modifiers;
        this.implementedInterfaces = implementedInterfaces;
        this.methods = methods;
        this.block = block;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        super.accept(visitor, data);

        for (final BaseInterfaceNode baseInterface : implementedInterfaces) {
            baseInterface.accept(visitor, data);
        }

        for (final FunctionDeclarationNode functionDeclarationNode : methods) {
            functionDeclarationNode.accept(visitor, data);
        }

        block.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterfaceNode that = (InterfaceNode) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(modifiers, that.modifiers)) return false;
        return Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (modifiers != null ? modifiers.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InterfaceNode{" +
            "name='" + name + '\'' +
            ", modifiers=" + modifiers +
            ", implementedInterfaces=" + implementedInterfaces +
            ", block=" + block +
            '}';
    }
}
