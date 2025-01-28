package parser.nodes.classes;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;

import java.util.List;

public class InterfaceNode extends TypeDeclarationNode {
    public String name;
    public List<String> modifiers;
    public BlockNode block;

    public InterfaceNode(
        String name,
        List<String> modifiers,
        List<BaseInterfaceNode> implementedInterfaces,
        List<FunctionDeclarationNode> methods,
        BlockNode block
    ) {
        this.name = name;
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
    public String toString() {
        return "InterfaceNode{" +
            "name='" + name + '\'' +
            ", modifiers=" + modifiers +
            ", implementedInterfaces=" + implementedInterfaces +
            ", block=" + block +
            '}';
    }
}
