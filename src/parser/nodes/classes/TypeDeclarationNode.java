package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FlowType;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.scopes.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;

public abstract class TypeDeclarationNode implements ASTNode {
    public String name;
    public List<FunctionDeclarationNode> methods;
    public List<BaseInterfaceNode> implementedInterfaces;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeDeclarationNode that = (TypeDeclarationNode) o;

        if (!Objects.equals(methods, that.methods)) return false;
        return Objects.equals(implementedInterfaces, that.implementedInterfaces);
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
        FunctionDeclarationNode function = findMethodWithParameters(
            scope,
            methods,
            name,
            parameterTypes
        );

        return function;
    }

    @Override
    public int hashCode() {
        int result = methods != null ? methods.hashCode() : 0;
        result = 31 * result + (implementedInterfaces != null ? implementedInterfaces.hashCode() : 0);
        return result;
    }
}
