package parser.nodes.statements;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;

public record ForStatementNode(
    VariableDeclarationNode initialization,
    ExpressionNode condition,
    VariableAssignmentNode action,
    BlockNode loopBlock
) implements ASTNode {}
