package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.ReturnStatementNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.visitors.BlockTraverse;
import semantic_analysis.visitors.ExpressionTraverse;

import java.util.ArrayList;

import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;

public class FunctionLoader {
    public static void loadSignature(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        ModifierLoader.load(functionDeclarationNode, functionDeclarationNode.modifiers, ModifierLoader.ModifierType.FUNCTION);

        if (!scope.findTypeDeclaration(functionDeclarationNode.returnType)) {
            LoggerFacade.error("Unresolved symbol: '" + functionDeclarationNode.returnType + "'", functionDeclarationNode);
            return;
        }

        if (
            findMethodWithParameters(
                scope,
                scope.symbols().functions(),
                functionDeclarationNode.name,
                functionDeclarationNode.parameters.stream()
                    .map(parameterNode -> new ExpressionTraverse.TypeWrapper(parameterNode.type, false, parameterNode.isNullable)).toList()
            ) != null
        ) {
            LoggerFacade.error("Conflicting overloads for: '" + functionDeclarationNode.name + "'", functionDeclarationNode);
        }

        for (final ParameterNode parameter : functionDeclarationNode.parameters) {
            if (!scope.findTypeDeclaration(parameter.type)) {
                LoggerFacade.error("Unresolved symbol: '" + parameter.type + "'", parameter);
                return;
            }
        }

        if (scope.type() == Scope.Type.TOP && !functionDeclarationNode.modifiers.contains("static")) {
            functionDeclarationNode.modifiers.add("static");
        }

        scope.symbols().functions().add(functionDeclarationNode);
    }

    public static void loadBody(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        final SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        for (final ParameterNode parameter : functionDeclarationNode.parameters) {
            symbolTable.fields().add(
                new FieldNode(
                    new ArrayList<>(),
                    new InitializedVariableNode(
                        new VariableDeclarationNode(
                            "var",
                            parameter.type,
                            parameter.name,
                            parameter.isNullable
                        ),
                        null
                    )
                )
            );
        }

        if (functionDeclarationNode.block != null) {
            BlockTraverse.traverse(functionDeclarationNode.block, new Scope(scope, symbolTable, functionDeclarationNode, Scope.Type.FUNCTION));

            if (!functionDeclarationNode.returnType.equals("Void")) {
                boolean haveReturn = false;
                for (final ASTNode node : functionDeclarationNode.block.children) {
                    if (node instanceof ReturnStatementNode) {
                        haveReturn = true;
                        break;
                    }
                }

                if (!haveReturn) {
                    LoggerFacade.error("Missing return statement", functionDeclarationNode);
                }
            }
        }
    }
}
