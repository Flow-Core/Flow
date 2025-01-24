package parser.nodes;

public interface ASTVisitor<D> {
    void visit(final ASTNode node, final D data);
}
