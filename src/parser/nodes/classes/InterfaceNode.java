package parser.nodes.classes;

import parser.nodes.ASTNode;
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
}
