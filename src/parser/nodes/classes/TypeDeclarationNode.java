package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.functions.FunctionDeclarationNode;

import java.util.List;
import java.util.Objects;

public abstract class TypeDeclarationNode implements ASTNode {
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

    @Override
    public int hashCode() {
        int result = methods != null ? methods.hashCode() : 0;
        result = 31 * result + (implementedInterfaces != null ? implementedInterfaces.hashCode() : 0);
        return result;
    }
}
