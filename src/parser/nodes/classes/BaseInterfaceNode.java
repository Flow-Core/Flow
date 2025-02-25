package parser.nodes.classes;

import parser.nodes.ASTNode;

import java.util.Objects;

public class BaseInterfaceNode implements ASTNode {
    public String name;

    public BaseInterfaceNode(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseInterfaceNode that = (BaseInterfaceNode) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
