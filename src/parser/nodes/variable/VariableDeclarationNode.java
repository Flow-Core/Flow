package parser.nodes.variable;

import parser.nodes.ASTNode;
import parser.nodes.FlowType;

import java.util.Objects;

public class VariableDeclarationNode implements ASTNode {
    public String modifier;
    public FlowType type;
    public String name;

    public VariableDeclarationNode(String modifier, FlowType type, String name) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableDeclarationNode that = (VariableDeclarationNode) o;

        if (!Objects.equals(modifier, that.modifier)) return false;
        if (!Objects.equals(type, that.type)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = modifier != null ? modifier.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return modifier + " " + name + ": " + type;
    }
}
