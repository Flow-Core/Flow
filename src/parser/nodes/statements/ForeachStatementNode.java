package parser.nodes.statements;

import parser.nodes.ASTNode;

public record ForeachStatementNode(
    ASTNode loopVariable,
    ASTNode collection,
    ASTNode body
) implements ASTNode {}