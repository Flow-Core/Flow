package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FlowType;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;
import semantic_analysis.visitors.ParameterTraverse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TypeDeclarationNode implements ASTNode {
    public String name;
    public List<FunctionDeclarationNode> methods;
    public List<BaseInterfaceNode> implementedInterfaces;
    public List<TypeParameterNode> typeParameters;

    public List<FunctionDeclarationNode> getAllMethods(Scope scope) {
        final List<FunctionDeclarationNode> methods = new ArrayList<>(this.methods);

        if (this instanceof ClassDeclarationNode classDeclarationNode && !classDeclarationNode.baseClasses.isEmpty()) {
            final String baseClassName = classDeclarationNode.baseClasses.get(0).type.name;
            final ClassDeclarationNode baseClass = TypeRecognize.getClass(baseClassName, scope);
            if (baseClass == null) {
                throw new IllegalArgumentException("Base class '" + baseClassName + "' not found in scope");
            }

            methods.addAll(baseClass.getAllMethods(scope));
        }

        for (final BaseInterfaceNode baseInterfaceNode : implementedInterfaces) {
            final InterfaceNode interfaceNode = TypeRecognize.getInterface(baseInterfaceNode.type.name, scope);
            if (interfaceNode == null) {
                throw new IllegalArgumentException("Interface '" + baseInterfaceNode.type.name + "' not found in scope");
            }

            methods.addAll(interfaceNode.getAllMethods(scope));
        }

        return methods;
    }

    public List<FunctionDeclarationNode> getAllSuperFunctions(Scope scope) {
        final List<FunctionDeclarationNode> allFunctions = new ArrayList<>();

        if (this instanceof ClassDeclarationNode classDeclaration) {
            if (!classDeclaration.baseClasses.isEmpty()) {
                final ClassDeclarationNode baseClass = TypeRecognize.getClass(classDeclaration.baseClasses.get(0).type.name, scope);
                if (baseClass == null) {
                    throw new IllegalArgumentException("Base class not found");
                }

                allFunctions.addAll(baseClass.getAllMethods(scope));
            }
        }

        for (final BaseInterfaceNode baseInterfaceNode : implementedInterfaces) {
            final InterfaceNode baseInterface = TypeRecognize.getInterface(baseInterfaceNode.type.name, scope);
            if (baseInterface == null) {
                throw new IllegalArgumentException("Base interface not found");
            }

            allFunctions.addAll(baseInterface.getAllMethods(scope));
        }

        return allFunctions;
    }

    public List<FunctionDeclarationNode> findMethodsWithName(
        Scope scope,
        String name
    ) {
        TypeDeclarationNode caller = this;

        return new ArrayList<>(caller.methods.stream()
            .filter(
                method -> method.name.equals(name)
            ).toList());
    }

    public FunctionDeclarationNode findMethod(
        Scope scope,
        String name,
        List<FlowType> parameterTypes
    ) {
        return ParameterTraverse.findMethodWithParameters(
            scope,
            methods,
            name,
            parameterTypes
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TypeDeclarationNode that = (TypeDeclarationNode) o;
        return Objects.equals(name, that.name) && Objects.equals(methods, that.methods) && Objects.equals(implementedInterfaces, that.implementedInterfaces) && Objects.equals(typeParameters, that.typeParameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(methods);
        result = 31 * result + Objects.hashCode(implementedInterfaces);
        result = 31 * result + Objects.hashCode(typeParameters);
        return result;
    }
}