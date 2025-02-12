package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.variable.InitializedVariableNode;

import java.util.List;
import java.util.Objects;

public class FieldNode implements ASTNode {
    public List<String> modifiers;
    public InitializedVariableNode initialization;
    public boolean isInitialized;

    public FieldNode(List<String> modifiers, InitializedVariableNode initialization
    ) {
        this.modifiers = modifiers;
        this.initialization = initialization;

        isInitialized = false;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, D data) {
        ASTNode.super.accept(visitor, data);

        if (initialization != null) {
            initialization.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldNode fieldNode = (FieldNode) o;

        if (isInitialized != fieldNode.isInitialized) return false;
        if (!Objects.equals(modifiers, fieldNode.modifiers)) return false;
        return Objects.equals(initialization, fieldNode.initialization);
    }

    @Override
    public int hashCode() {
        int result = modifiers != null ? modifiers.hashCode() : 0;
        result = 31 * result + (initialization != null ? initialization.hashCode() : 0);
        result = 31 * result + (isInitialized ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldNode{" +
            "modifiers=" + modifiers +
            ", initialization=" + initialization +
            '}';
    }
}
