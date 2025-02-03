package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.statements.StatementNode;
import parser.nodes.variable.VariableAssignmentNode;
import semantic_analysis.loaders.VariableLoader;
import semantic_analysis.scopes.Scope;

public class BlockTraverse {
    public static void traverse(final BlockNode block, final Scope scope) {
        for (final ASTNode node : block.children) {
            if (node instanceof StatementNode statementNode) {
                StatementTraverse.traverse(statementNode, scope);
            } else if (node instanceof FieldNode fieldNode) {
                VariableLoader.loadDeclaration(fieldNode, scope);
            } else if (node instanceof VariableAssignmentNode variableAssignmentNode) {
                VariableLoader.loadAssignment(variableAssignmentNode, scope);
            } else if (node instanceof ExpressionBaseNode expressionBaseNode) {
                new ExpressionTraverse().traverse(expressionBaseNode, scope);
            }
        }
    }
}