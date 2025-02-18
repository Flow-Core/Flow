package semantic_analysis.visitors;

import logger.LoggerFacade;
import parser.nodes.ASTNode;
import parser.nodes.FlowType;
import parser.nodes.classes.FieldNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.*;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.VariableReferenceNode;
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
        final FlowType conditionType = new ExpressionTraverse().traverse(ifStatementNode.condition, scope);
        if (conditionType == null || !conditionType.name.equals("Bool") && !conditionType.isNullable) {
            LoggerFacade.error("Condition type mismatch: 'Bool' was expected", ifStatementNode);
        }

        final Scope trueBranchScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);
        BlockTraverse.traverse(ifStatementNode.trueBranch.blockNode, trueBranchScope);
        ifStatementNode.trueBranch.scope = trueBranchScope;

        if (ifStatementNode.falseBranch != null) {
            final Scope falseBranchScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);
            BlockTraverse.traverse(ifStatementNode.falseBranch.blockNode, falseBranchScope);
        }
    }

    private static void handleWhileStatement(final WhileStatementNode whileStatementNode, final Scope scope) {
        final FlowType conditionType = new ExpressionTraverse().traverse(whileStatementNode.condition, scope);
        if (conditionType == null || !conditionType.name.equals("Bool") && !conditionType.isNullable) {
            LoggerFacade.error("Condition type mismatch: 'Bool' was expected", whileStatementNode);
        }

        final Scope whileScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);
        BlockTraverse.traverse(whileStatementNode.loopBlock.blockNode, whileScope);
        whileStatementNode.loopBlock.scope = whileScope;
    }

    private static void handleForStatement(final ForStatementNode forStatementNode, final Scope scope) {
        final Scope forScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);

        final FlowType varType = new ExpressionTraverse().traverse(forStatementNode.initialization.value, scope);

        if (varType == null)
            return;

        final FieldNode localVariable = new FieldNode(
            new ArrayList<>(),
            new InitializedVariableNode(
                new VariableDeclarationNode(
                    "var",
                    varType,
                    ((VariableReferenceNode) forStatementNode.initialization.variable.expression).variable
                ),
                forStatementNode.initialization
            )
        );
        forScope.symbols().fields().add(localVariable);
        forStatementNode.populatedInitialization = localVariable;

        final FlowType conditionType = new ExpressionTraverse().traverse(forStatementNode.condition, forScope);
        if (conditionType == null) {
            return;
        }

        if (!conditionType.name.equals("Bool") && !conditionType.isNullable) {
            LoggerFacade.error("Loop condition type mismatch: must be of type 'Bool'", forStatementNode.condition);
        }

        BlockTraverse.traverse(forStatementNode.action.blockNode, forScope);
        BlockTraverse.traverse(forStatementNode.loopBlock.blockNode, forScope);

        forStatementNode.action.scope = forScope;
        forStatementNode.loopBlock.scope = forScope;
    }

    private static void handleSwitchStatement(final SwitchStatementNode switchStatementNode, final Scope scope) {
        final FlowType switchType = new ExpressionTraverse().traverse(switchStatementNode.condition, scope);

        if (switchType == null)
            return;

        for (CaseNode caseNode : switchStatementNode.cases) {
            final FlowType caseType = new ExpressionTraverse().traverse(caseNode.value, scope);

            if (caseType == null)
                continue;

            if (!scope.isSameType(caseType, switchType)) {
                LoggerFacade.error("Switch case type mismatch: Expected '" + switchType + "' but found '" + caseType + "'", caseNode);
            }

            final Scope caseScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);
            BlockTraverse.traverse(caseNode.body.blockNode, caseScope);
            caseNode.body.scope = caseScope;
        }

        if (switchStatementNode.defaultBlock != null) {
            final Scope defaultScope = new Scope(scope, SymbolTable.getEmptySymbolTable(), scope.currentParent(), Scope.Type.FUNCTION);
            BlockTraverse.traverse(switchStatementNode.defaultBlock.blockNode, defaultScope);
            switchStatementNode.defaultBlock.scope = defaultScope;
        }
    }

    private static void handleReturnStatement(final ReturnStatementNode returnStatementNode, final Scope scope) {
        final FlowType returnType = new ExpressionTraverse().traverse(returnStatementNode.returnValue, scope);

        if (returnType == null)
            return;

        final ASTNode currentParent = scope.currentParent();
        if (!(currentParent instanceof FunctionDeclarationNode functionDeclarationNode)) {
            LoggerFacade.error("Return statement is not allowed here", returnStatementNode);
            return;
        }

        if (returnType.name.equals("null")) {
            if (!functionDeclarationNode.returnType.isNullable) {
                LoggerFacade.error("Null cannot be a value of a non-null type '" + functionDeclarationNode.returnType + "'", returnStatementNode);
            }
        } else if (!scope.isSameType(
            returnType,
            functionDeclarationNode.returnType
        )) {
            LoggerFacade.error("Type mismatch: expected '"  + functionDeclarationNode.returnType + "' but received '" + returnType + "'", returnStatementNode);
        }
    }
}
