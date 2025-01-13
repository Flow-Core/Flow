package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FunctionDeclarationNode;

import java.util.List;

public class InterfaceNode implements ASTNode {
    public String name;
    public List<String> modifiers;
    public List<BaseInterfaceNode> implementedInterfaces;
    public List<FunctionDeclarationNode> methods;

    public InterfaceNode(String name, List<String> modifiers, List<BaseInterfaceNode> implementedInterfaces, List<FunctionDeclarationNode> methods
    ) {
        this.name = name;
        this.modifiers = modifiers;
        this.implementedInterfaces = implementedInterfaces;
        this.methods = methods;
    }
}
