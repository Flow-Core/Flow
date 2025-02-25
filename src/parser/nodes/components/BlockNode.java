package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;

import java.util.List;
import java.util.Objects;

public class BlockNode implements ASTNode {
    public List<ASTNode> children;

    public BlockNode(List<ASTNode> children) {
        this.children = children;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        for (final ASTNode child : children) {
            child.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockNode blockNode = (BlockNode) o;

        return Objects.equals(children, blockNode.children);
    }

    @Override
    public int hashCode() {
        return children != null ? children.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("{\n");

        for (ASTNode node : children) {
            output.append(node.toString()).append("\n");
        }

        output.append("}");

        return output.toString();
    }
}
