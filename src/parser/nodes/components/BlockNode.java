package parser.nodes.components;

import parser.nodes.ASTNode;

import java.util.List;

public record BlockNode(List<ASTNode> children) implements ASTNode {

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (ASTNode node : children) {
            output.append(node.toString());
        }

        return output.toString();
    }
}
