package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;

import java.util.List;

public class InterfaceNode implements ASTNode {
    public String name;
    public List<String> modifiers;
    public List<BaseInterfaceNode> implementedInterfaces;
    public BlockNode block;

    public InterfaceNode(
        String name,
        List<String> modifiers,
        List<BaseInterfaceNode> implementedInterfaces,
        BlockNode block
    ) {
        this.name = name;
        this.modifiers = modifiers;
        this.implementedInterfaces = implementedInterfaces;
        this.block = block;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        for (final BaseInterfaceNode baseInterface : implementedInterfaces) {
            baseInterface.accept(visitor, data);
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
