package parser.nodes;

public interface ASTNode {
    default void accept(final ASTVisitor visitor) {
        visitor.visit(this);
    }
}
