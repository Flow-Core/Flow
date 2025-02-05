package generators.ast.classes;

import parser.nodes.classes.InterfaceNode;
import parser.nodes.classes.BaseInterfaceNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class InterfaceNodeGenerator {
    private String name = "TestInterface";
    private List<String> modifiers = new ArrayList<>();
    private List<BaseInterfaceNode> implementedInterfaces = new ArrayList<>();
    private List<FunctionDeclarationNode> methods = new ArrayList<>();
    private BlockNode block = new BlockNode(new ArrayList<>());

    public static InterfaceNodeGenerator builder() {
        return new InterfaceNodeGenerator();
    }

    public InterfaceNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public InterfaceNodeGenerator modifiers(List<String> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public InterfaceNodeGenerator implementedInterfaces(List<BaseInterfaceNode> interfaces) {
        this.implementedInterfaces = interfaces;
        return this;
    }

    public InterfaceNodeGenerator methods(List<FunctionDeclarationNode> methods) {
        this.methods = methods;
        return this;
    }

    public InterfaceNodeGenerator block(BlockNode block) {
        this.block = block;
        return this;
    }

    public InterfaceNode build() {
        return new InterfaceNode(name, modifiers, implementedInterfaces, methods, block);
    }
}