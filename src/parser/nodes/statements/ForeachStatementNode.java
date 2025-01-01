package parser.nodes.statements;

import parser.nodes.ASTNode;

public class ForeachStatementNode implements ASTNode {
    private final ASTNode loopVariable;
    private final ASTNode collection;
    private final ASTNode body;

    public ForeachStatementNode(
        final ASTNode loopVariable,
        final ASTNode collection,
        final ASTNode body
    ) {
        this.loopVariable = loopVariable;
        this.collection = collection;
        this.body = body;
    }

    public ASTNode getLoopVariable() {
        return loopVariable;
    }

    public ASTNode getCollection() {
        return collection;
    }

    public ASTNode getBody() {
        return body;
    }
}