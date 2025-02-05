package parser.nodes.classes;

import parser.nodes.ASTVisitor;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.scopes.Scope;

import java.util.ArrayList;
import java.util.List;

import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;

public class ClassDeclarationNode extends TypeDeclarationNode {
    public String name;
    public List<String> modifiers;
    public List<FieldNode> primaryConstructor;
    public List<BaseClassNode> baseClasses;
    public List<FieldNode> fields;
    public List<ConstructorNode> constructors;
    public BlockNode initBlock;
    public BlockNode classBlock;

    public ClassDeclarationNode(
        final String name,
        final List<String> modifiers,
        final List<FieldNode> primaryConstructor,
        final List<BaseClassNode> baseClasses,
        final List<BaseInterfaceNode> implementedInterfaces,
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
        this.implementedInterfaces = implementedInterfaces;
        this.fields = fields;
        this.methods = methods;
        this.constructors = constructors;
        this.initBlock = initBlock;
        this.classBlock = classBlock;
    }

    public List<FunctionDeclarationNode> findMethodsWithName(
        Scope scope,
        String name
    ) {
        ClassDeclarationNode caller = this;
        List<FunctionDeclarationNode> functions = new ArrayList<>();

        while (caller != null) {
            functions.addAll(caller.methods.stream()
                .filter(
                    method -> method.name.equals(name)
                ).toList());

            if (!caller.baseClasses.isEmpty())
                caller = scope.getClass(caller.baseClasses.get(0).name);
            else
                break;
        }

        return functions;
    }

    public FunctionDeclarationNode findMethod(
        Scope scope,
        String name,
        List<String> parameterTypes
    ) {
        ClassDeclarationNode caller = this;
        FunctionDeclarationNode function = findMethodWithParameters(
            scope,
            methods,
            name,
            parameterTypes
        );

        while (function == null && caller != null && !caller.baseClasses.isEmpty()) {
            function = findMethodWithParameters(
                scope,
                caller.methods,
                name,
                parameterTypes
            );

            caller = scope.getClass(caller.baseClasses.get(0).name);
        }

        return function;
    }

    private static FieldNode findField(
        List<FieldNode> fields,
        String name
    ) {
        return fields.stream().filter(
            interfaceNode -> interfaceNode.initialization.declaration.name.equals(name)
        ).findFirst().orElse(null);
    }

    public FieldNode findField(
        Scope scope,
        String name
    ) {
        ClassDeclarationNode caller = this;
        FieldNode field = findField(caller.fields, name);

        while (field == null && caller != null && !caller.baseClasses.isEmpty()) {
            field = findField(caller.fields, name);

            caller = scope.getClass(caller.baseClasses.get(0).name);
        }

        return field;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, D data) {
        super.accept(visitor, data);

        for (final FieldNode field : primaryConstructor) {
            field.accept(visitor, data);
        }

        for (final BaseClassNode baseClass : baseClasses) {
            baseClass.accept(visitor, data);
        }

        for (final BaseInterfaceNode baseInterface : implementedInterfaces) {
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
            ", interfaces=" + implementedInterfaces +
            ", fields=" + fields +
            ", methods=" + methods +
            ", constructors=" + constructors +
            ", initBlock=" + initBlock +
            ", classBlock=" + classBlock +
            '}';
    }
}