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
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        for (final BaseInterfaceNode baseInterface : implementedInterfaces) {
            baseInterface.accept(visitor);
        }

        block.accept(visitor);
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
