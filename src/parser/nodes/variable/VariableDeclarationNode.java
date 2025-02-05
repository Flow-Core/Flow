package parser.nodes.variable;

import parser.nodes.ASTNode;

import java.util.Objects;

public class VariableDeclarationNode implements ASTNode {
    public String modifier;
    public String type;
    public String name;
    public boolean isNullable;

    public VariableDeclarationNode(String modifier, String type, String name, boolean isNullable) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
        this.isNullable = isNullable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableDeclarationNode that = (VariableDeclarationNode) o;

        if (isNullable != that.isNullable) return false;
        if (!Objects.equals(modifier, that.modifier)) return false;
        if (!Objects.equals(type, that.type)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = modifier != null ? modifier.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isNullable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VariableDeclarationNode{" +
                "modifier='" + modifier + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", isNullable=" + isNullable +
                '}';
    }
}
