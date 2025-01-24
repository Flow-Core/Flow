package parser.nodes;

public interface ASTNode {
    default <D> void accept(final ASTVisitor<D> visitor, final D data) {
        visitor.visit(this, data);
    }
}
