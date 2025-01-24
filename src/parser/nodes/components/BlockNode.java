package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;

import java.util.List;

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
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (ASTNode node : children) {
            output.append(node.toString()).append("\n");
        }

        return output.toString();
    }
}
