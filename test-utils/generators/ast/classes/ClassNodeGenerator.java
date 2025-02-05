package generators.ast.classes;

import parser.nodes.classes.*;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class ClassNodeGenerator {
    private String name = "TestClass";
    private List<String> modifiers = new ArrayList<>();
    private List<FieldNode> primaryConstructor = new ArrayList<>();
    private List<BaseClassNode> baseClasses = new ArrayList<>();
    private List<BaseInterfaceNode> implementedInterfaces = new ArrayList<>();
    private List<FieldNode> fields = new ArrayList<>();
    private List<FunctionDeclarationNode> methods = new ArrayList<>();
    private List<ConstructorNode> constructors = new ArrayList<>();
    private BlockNode initBlock = new BlockNode(new ArrayList<>());
    private BlockNode classBlock = new BlockNode(new ArrayList<>());

    public static ClassNodeGenerator builder() {
        return new ClassNodeGenerator();
    }

    public ClassNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public ClassNodeGenerator modifiers(List<String> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public ClassNodeGenerator primaryConstructor(List<FieldNode> primaryConstructor) {
        this.primaryConstructor = primaryConstructor;
        return this;
    }

    public ClassNodeGenerator baseClasses(List<BaseClassNode> baseClasses) {
        this.baseClasses = baseClasses;
        return this;
    }

    public ClassNodeGenerator implementedInterfaces(List<BaseInterfaceNode> interfaces) {
        this.implementedInterfaces = interfaces;
        return this;
    }

    public ClassNodeGenerator fields(List<FieldNode> fields) {
        this.fields = fields;
        return this;
    }

    public ClassNodeGenerator methods(List<FunctionDeclarationNode> methods) {
        this.methods = methods;
        return this;
    }

    public ClassNodeGenerator constructors(List<ConstructorNode> constructors) {
        this.constructors = constructors;
        return this;
    }

    public ClassNodeGenerator initBlock(BlockNode initBlock) {
        this.initBlock = initBlock;
        return this;
    }

    public ClassNodeGenerator classBlock(BlockNode classBlock) {
        this.classBlock = classBlock;
        return this;
    }

    public ClassDeclarationNode build() {
        return new ClassDeclarationNode(
            name, modifiers, primaryConstructor, baseClasses,
            implementedInterfaces, fields, methods, constructors, initBlock, classBlock
        );
    }
}
