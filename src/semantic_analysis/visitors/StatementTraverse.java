package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.*;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

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
        final ExpressionTraverse.TypeWrapper conditionType = new ExpressionTraverse().traverse(ifStatementNode.condition, scope);
        if (!conditionType.type().equals("Bool") && !conditionType.isNullable()) {
            throw new SA_SemanticError("Condition type mismatch: 'Bool' was expected");
        }

        BlockTraverse.traverse(ifStatementNode.trueBranch, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));

        if (ifStatementNode.falseBranch != null) {
            BlockTraverse.traverse(ifStatementNode.falseBranch, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));
        }
    }

    private static void handleWhileStatement(final WhileStatementNode whileStatementNode, final Scope scope) {
        final ExpressionTraverse.TypeWrapper conditionType = new ExpressionTraverse().traverse(whileStatementNode.condition, scope);
        if (!conditionType.type().equals("Bool") && !conditionType.isNullable()) {
            throw new SA_SemanticError("Loop condition type mismatch: must be of type 'Bool'");
        }

        BlockTraverse.traverse(whileStatementNode.loopBlock, new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION));
    }

    private static void handleForStatement(final ForStatementNode forStatementNode, final Scope scope) {
        final Scope forScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);

        final ExpressionTraverse.TypeWrapper varType = new ExpressionTraverse().traverse(forStatementNode.initialization.value, scope);

        final FieldNode localVariable = new FieldNode(
            new ArrayList<>(),
            new InitializedVariableNode(
                new VariableDeclarationNode(
                    "var",
                    varType.type(),
                    ((VariableReferenceNode) forStatementNode.initialization.variable.expression).variable,
                    varType.isNullable()
                ),
                forStatementNode.initialization
            )
        );
        forScope.symbols().fields().add(localVariable);
        forStatementNode.populatedInitialization = localVariable;

        final ExpressionTraverse.TypeWrapper conditionType = new ExpressionTraverse().traverse(forStatementNode.condition, forScope);
        if (!conditionType.type().equals("Bool") && !conditionType.isNullable()) {
            throw new SA_SemanticError("Loop condition type mismatch: must be of type 'Bool'");
        }

        BlockTraverse.traverse(forStatementNode.action, forScope);
        BlockTraverse.traverse(forStatementNode.loopBlock, forScope);
    }

    private static void handleSwitchStatement(final SwitchStatementNode switchStatementNode, final Scope scope) {
        final ExpressionTraverse.TypeWrapper switchType = new ExpressionTraverse().traverse(switchStatementNode.condition, scope);

        for (CaseNode caseNode : switchStatementNode.cases) {
            final ExpressionTraverse.TypeWrapper caseType = new ExpressionTraverse().traverse(caseNode.value, scope);

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
        final ExpressionTraverse.TypeWrapper returnType = new ExpressionTraverse().traverse(returnStatementNode.returnValue, scope);
        final ASTNode currentParent = scope.currentParent();
        if (!(currentParent instanceof FunctionDeclarationNode functionDeclarationNode)) {
            throw new SA_SemanticError("Return statement is not allowed here");
        }

        if (returnType.type().equals("null")) {
            if (!functionDeclarationNode.isReturnTypeNullable) {
                throw new SA_SemanticError("Null cannot be a value of a non-null type '" + functionDeclarationNode.returnType + "'");
            }
        } else if (!scope.isSameType(
            returnType,
            new ExpressionTraverse.TypeWrapper(
                functionDeclarationNode.returnType,
                false,
                functionDeclarationNode.isReturnTypeNullable)
        )) {
            throw new SA_SemanticError("Type mismatch: expected '"  + functionDeclarationNode.returnType + "' but received '" + returnType + "'");
        }
    }
}
