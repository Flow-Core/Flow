package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.*;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.scopes.Scope;

import java.util.ArrayList;

public class StatementTraverse {
    public static void traverse(final StatementNode statement, final Scope scope) {
        if (statement instanceof IfStatementNode ifStatementNode) {
            handleIfStatement(ifStatementNode, scope);
        } else if (statement instanceof WhileStatementNode whileStatementNode) {
            handleWhileStatement(whileStatementNode, scope);
        } else if (statement instanceof ForStatementNode forStatementNode) {
            handleForStatement(forStatementNode, scope);
        } else if (statement instanceof SwitchStatementNode switchStatementNode) {
            handleSwitchStatement(switchStatementNode, scope);
        } else if (statement instanceof ReturnStatementNode returnStatementNode) {
            handleReturnStatement(returnStatementNode, scope);
        }
    }

    private static void handleIfStatement(final IfStatementNode ifStatementNode, final Scope scope) {
        final String conditionType = new ExpressionTraverse().traverse(new ExpressionBaseNode(ifStatementNode.condition), scope);
        if (!conditionType.equals("Bool")) {
            throw new SA_SemanticError("Condition type mismatch: 'Bool' was expected");
        }

        BlockTraverse.traverse(ifStatementNode.trueBranch, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));

        if (ifStatementNode.falseBranch != null) {
            BlockTraverse.traverse(ifStatementNode.falseBranch, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));
        }
    }

    private static void handleWhileStatement(final WhileStatementNode whileStatementNode, final Scope scope) {
        final String conditionType = new ExpressionTraverse().traverse(new ExpressionBaseNode(whileStatementNode.condition), scope);
        if (!conditionType.equals("Bool")) {
            throw new SA_SemanticError("Loop condition type mismatch: must be of type 'Bool'");
        }

        BlockTraverse.traverse(whileStatementNode.loopBlock, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));
    }

    private static void handleForStatement(final ForStatementNode forStatementNode, final Scope scope) {
        final String conditionType = new ExpressionTraverse().traverse(new ExpressionBaseNode(forStatementNode.condition), scope);
        if (!conditionType.equals("Bool")) {
            throw new SA_SemanticError("Loop condition type mismatch: must be of type 'Bool'");
        }

        final Scope forScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);

        final FieldNode localVariable = new FieldNode(
            new ArrayList<>(),
            new InitializedVariableNode(
                new VariableDeclarationNode(
                    "var",
                    new ExpressionTraverse().traverse(forStatementNode.action.value, scope),
                    forStatementNode.initialization.variable,
                    false
                ),
                forStatementNode.initialization
            )
        );
        forScope.symbols().fields().add(localVariable);

        BlockTraverse.traverse(forStatementNode.loopBlock, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));
    }

    private static void handleSwitchStatement(final SwitchStatementNode switchStatementNode, final Scope scope) {
        final String switchType = new ExpressionTraverse().traverse(new ExpressionBaseNode(switchStatementNode.condition), scope);

        for (CaseNode caseNode : switchStatementNode.cases) {
            final String caseType = new ExpressionTraverse().traverse(new ExpressionBaseNode(caseNode.value), scope);

            if (!scope.isSameType(caseType, switchType)) {
                throw new SA_SemanticError("Switch case type mismatch: Expected '" + switchType + "' but found '" + caseType + "'");
            }

            BlockTraverse.traverse(caseNode.body, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));
        }

        if (switchStatementNode.defaultBlock != null) {
            BlockTraverse.traverse(switchStatementNode.defaultBlock, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));
        }
    }

    private static void handleReturnStatement(final ReturnStatementNode returnStatementNode, final Scope scope) {
        final String returnType = new ExpressionTraverse().traverse(new ExpressionBaseNode(returnStatementNode.returnValue), scope); // TODO: Expression base
        final ASTNode currentParent = scope.currentParent();
        if (!(currentParent instanceof FunctionDeclarationNode functionDeclarationNode)) {
            throw new SA_SemanticError("Return statement is not allowed here");
        }

        if (returnType.equals("null")) {
            if (!functionDeclarationNode.isReturnTypeNullable) {
                throw new SA_SemanticError("Null cannot be a value of a non-null type '" + functionDeclarationNode.returnType + "'");
            }
        } else if (!scope.isSameType(returnType, functionDeclarationNode.returnType)) {
            throw new SA_SemanticError("Type mismatch: expected '"  + functionDeclarationNode.returnType + "' but received '" + returnType + "'");
        }
    }
}
