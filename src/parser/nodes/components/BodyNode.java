package parser.nodes.components;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import semantic_analysis.scopes.Scope;

import java.util.Objects;

public class BodyNode implements ASTNode {
    public BlockNode blockNode;
    public Scope scope;

    public BodyNode(BlockNode blockNode) {
        this.blockNode = blockNode;
    }

    public BodyNode(BlockNode blockNode, Scope scope) {
        this.blockNode = blockNode;
        this.scope = scope;
    }

    @Override
    public <D> void accept(ASTVisitor<D> visitor, D data) {
        ASTNode.super.accept(visitor, data);

        if (blockNode != null) {
            blockNode.accept(visitor, data);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BodyNode bodyNode = (BodyNode) o;
        return Objects.equals(blockNode, bodyNode.blockNode) && Objects.equals(scope, bodyNode.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockNode, scope);
    }

    @Override
    public String toString() {
        return "BodyNode{" +
            "blockNode=" + blockNode +
            ", scope=" + scope +
            '}';
    }
}
