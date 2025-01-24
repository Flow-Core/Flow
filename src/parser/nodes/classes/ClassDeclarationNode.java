package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.components.BlockNode;

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

    public ClassDeclarationNode(
        final String name,
        final List<String> modifiers,
        final List<FieldNode> primaryConstructor,
        final List<BaseClassNode> baseClasses,
        final List<BaseInterfaceNode> interfaces,
        final List<FieldNode> fields,
        final List<FunctionDeclarationNode> methods,
        final List<ConstructorNode> constructors,
        final BlockNode initBlock
    ) {
        this.name = name;
        this.modifiers = modifiers;
        this.primaryConstructor = primaryConstructor;
        this.baseClasses = baseClasses;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.constructors = constructors;
        this.initBlock = initBlock;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        for (final FieldNode field : primaryConstructor) {
            field.accept(visitor);
        }

        for (final BaseClassNode baseClass : baseClasses) {
            baseClass.accept(visitor);
        }

        for (final BaseInterfaceNode baseInterface : interfaces) {
            baseInterface.accept(visitor);
        }

        for (final FieldNode field : fields) {
            field.accept(visitor);
        }

        for (final FunctionDeclarationNode method : methods) {
            method.accept(visitor);
        }

        for (final ConstructorNode constructorNode : constructors) {
            constructorNode.accept(visitor);
        }

        initBlock.accept(visitor);
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
            '}';
    }
}