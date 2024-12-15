package parser.nodes;

import java.util.List;

public class BlockNode implements ASTNode {
    private final List<ASTNode> children;

    public BlockNode(final List<ASTNode> children) {
        this.children = children;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (ASTNode node : children) {
            output.append(node.toString());
        }

        return output.toString();
    }
}
