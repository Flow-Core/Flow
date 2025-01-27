package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclarationNode implements ASTNode {
    public String name;
    public List<String> modifiers;
    public List<FieldNode> primaryConstructor;
    public List<BaseClassNode> baseClasses;
    public List<BaseInterfaceNode> interfaces;
    public List<FieldNode> fields;
    public List<FunctionDeclarationNode> methods;
    public List<ConstructorNode> constructors;
    public BlockNode initBlock;
    public BlockNode classBlock;

    public ClassDeclarationNode(
        final String name,
        final List<String> modifiers,
        final List<FieldNode> primaryConstructor,
        final List<BaseClassNode> baseClasses,
        final List<BaseInterfaceNode> interfaces,
        final List<FieldNode> fields,
        final List<FunctionDeclarationNode> methods,
        final List<ConstructorNode> constructors,
        final BlockNode initBlock,
        final BlockNode classBlock
    ) {
        this.name = name;
        this.modifiers = modifiers != null ? modifiers : new ArrayList<>();
        this.primaryConstructor = primaryConstructor;
        this.baseClasses = baseClasses;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.constructors = constructors;
        this.initBlock = initBlock;
        this.classBlock = classBlock;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, D data) {
        ASTNode.super.accept(visitor, data);

        for (final FieldNode field : primaryConstructor) {
            field.accept(visitor, data);
        }

        for (final BaseClassNode baseClass : baseClasses) {
            baseClass.accept(visitor, data);
        }

        for (final BaseInterfaceNode baseInterface : interfaces) {
            baseInterface.accept(visitor, data);
        }

        for (final FieldNode field : fields) {
            field.accept(visitor, data);
        }

        for (final FunctionDeclarationNode method : methods) {
            method.accept(visitor, data);
        }

        for (final ConstructorNode constructorNode : constructors) {
            constructorNode.accept(visitor, data);
        }

        if (initBlock != null) {
            initBlock.accept(visitor, data);
        }

        classBlock.accept(visitor, data);
    }

    @Override
    public String toString() {
        return "ClassDeclarationNode{" +
            "name='" + name + '\'' +
            ", modifiers=" + modifiers +
            ", primaryConstructor=" + primaryConstructor +
            ", baseClasses=" + baseClasses +
            ", interfaces=" + interfaces +
            ", fields=" + fields +
            ", methods=" + methods +
            ", constructors=" + constructors +
            ", initBlock=" + initBlock +
            ", classBlock=" + classBlock +
            '}';
    }
}