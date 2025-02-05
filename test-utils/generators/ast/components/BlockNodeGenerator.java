package generators.ast.components;

import parser.nodes.components.BlockNode;
import parser.nodes.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class BlockNodeGenerator {
    private final List<ASTNode> children = new ArrayList<>();

    public static BlockNodeGenerator builder() {
        return new BlockNodeGenerator();
    }

    public BlockNodeGenerator children(List<ASTNode> children) {
        this.children.addAll(children);
        return this;
    }

    public BlockNodeGenerator addChild(ASTNode node) {
        this.children.add(node);
        return this;
    }

    public BlockNode build() {
        return new BlockNode(children);
    }
}