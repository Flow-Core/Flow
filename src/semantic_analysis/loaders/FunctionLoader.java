package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.ReturnStatementNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.visitors.BlockTraverse;

import java.util.ArrayList;
import java.util.List;

import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;
import static semantic_analysis.visitors.ClassTraverse.addThisToSymbolTable;

public class FunctionLoader {
    public static void loadSignature(final FunctionDeclarationNode functionDeclarationNode, final Scope scope, boolean isInterface) {
        ModifierLoader.load(
            functionDeclarationNode.modifiers,
            isInterface ?
                ModifierLoader.ModifierType.FUNCTION_INTERFACE
                : ModifierLoader.ModifierType.FUNCTION
        );

        if (!scope.findTypeDeclaration(functionDeclarationNode.returnType.name())) {
            LoggerFacade.error("Unresolved symbol: '" + functionDeclarationNode.returnType + "'", functionDeclarationNode);
        }

        if (
            findMethodWithParameters(
                scope,
                scope.symbols().functions(),
                functionDeclarationNode.name,
                functionDeclarationNode.parameters.stream()
                    .map(parameterNode -> parameterNode.type).toList()
            ) != null
        ) {
            LoggerFacade.error("Conflicting overloads for: " + functionDeclarationNode.name, functionDeclarationNode);
        }

        checkParameters(functionDeclarationNode.parameters, scope);

        if (scope.type() == Scope.Type.TOP && !functionDeclarationNode.modifiers.contains("static")) {
            functionDeclarationNode.modifiers.add("static");
        }

        if (ModifierLoader.isDefaultPublic(functionDeclarationNode.modifiers)) {
            functionDeclarationNode.modifiers.add("public");
        }

        scope.symbols().functions().add(functionDeclarationNode);
    }

    public static void checkParameters(final List<ParameterNode> parameters, final Scope scope)  {
        for (final ParameterNode parameter : parameters) {
            if (!scope.findTypeDeclaration(parameter.type.name())) {
                LoggerFacade.error("Unresolved symbol: '" + parameter.type + "'", parameter);
            }
        }
    }

    public static void loadBody(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        final SymbolTable symbolTable = loadParameters(functionDeclarationNode.parameters);
        final TypeDeclarationNode containingType = scope.getContainingType();

        if (functionDeclarationNode.block == null) {
            return;
        }

        if (containingType != null && !functionDeclarationNode.modifiers.contains("static")) {
            addThisToSymbolTable(symbolTable, scope, containingType.name);
        }

        BlockTraverse.traverse(functionDeclarationNode.block, new Scope(scope, symbolTable, functionDeclarationNode, Scope.Type.FUNCTION));

        if (!functionDeclarationNode.returnType.name().equals("Void")) {
            boolean haveReturn = false;
            for (final ASTNode node : functionDeclarationNode.block.children) {
                if (node instanceof ReturnStatementNode) {
                    haveReturn = true;
                    break;
                }
            }

            if (!haveReturn) {
                LoggerFacade.error("Unresolved symbol: '" + functionDeclarationNode.returnType + "'", functionDeclarationNode);
            }
        }
    }

    public static SymbolTable loadParameters(final List<ParameterNode> parameters) {
        final SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        for (final ParameterNode parameter : parameters) {
            symbolTable.fields().add(
                new FieldNode(
                    new ArrayList<>(),
                    new InitializedVariableNode(
                        new VariableDeclarationNode(
                            "var",
                            parameter.type,
                            parameter.name
                        ),
                        null
                    )
                )
            );
        }

        return symbolTable;
    }
}
