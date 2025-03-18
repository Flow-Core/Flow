package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;
import parser.nodes.statements.ReturnStatementNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.scopes.TypeRecognize;
import semantic_analysis.visitors.BlockTraverse;
import semantic_analysis.visitors.ParameterTraverse;

import java.util.ArrayList;
import java.util.List;

import static semantic_analysis.visitors.ClassTraverse.addThisToSymbolTable;

public class FunctionLoader {
    public static void loadSignature(final FunctionDeclarationNode functionDeclarationNode, final Scope scope, boolean isInterface) {
        ModifierLoader.load(
            functionDeclarationNode.modifiers,
            isInterface ?
                ModifierLoader.ModifierType.FUNCTION_INTERFACE
                : ModifierLoader.ModifierType.FUNCTION
        );

        final SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        symbolTable.typeParameters().addAll(functionDeclarationNode.typeParameters);
        final Scope functionScope = new Scope(scope, symbolTable, functionDeclarationNode, Scope.Type.FUNCTION);
        functionDeclarationNode.body.scope = functionScope;

        loadTypeParameters(functionDeclarationNode, scope);

        if (!TypeRecognize.findTypeDeclaration(functionDeclarationNode.returnType.name, scope)) {
            LoggerFacade.error("Unresolved symbol: '" + functionDeclarationNode.returnType + "'", functionDeclarationNode);
        }

        if (
            ParameterTraverse.findMethodWithParameters(
                scope,
                scope.symbols().functions(),
                functionDeclarationNode.name,
                functionDeclarationNode.parameters.stream()
                    .map(parameterNode -> parameterNode.type).toList()
            ) != null
        ) {
            LoggerFacade.error("Conflicting overloads for: " + functionDeclarationNode.name, functionDeclarationNode);
        }

        final TypeDeclarationNode containingType = scope.getContainingType();
        if (containingType != null) {
            if (
                !functionDeclarationNode.modifiers.contains("override") &&
                ParameterTraverse.findMethodWithParameters(
                    scope,
                    containingType.getAllSuperFunctions(scope),
                    functionDeclarationNode.name,
                    functionDeclarationNode.parameters.stream()
                        .map(parameterNode -> parameterNode.type).toList()
                ) != null
            ) {
                LoggerFacade.error("'" + functionDeclarationNode.name + "' hides member of its supertype and needs an 'override' modifier", functionDeclarationNode);
            }
        }

        checkParameters(functionDeclarationNode.parameters, functionScope);
        symbolTable.recognizeSymbolTable(loadParameters(functionDeclarationNode.parameters));

        if (scope.type() == Scope.Type.TOP && !functionDeclarationNode.modifiers.contains("static")) {
            functionDeclarationNode.modifiers.add("static");
        }

        if (ModifierLoader.isDefaultPublic(functionDeclarationNode.modifiers)) {
            functionDeclarationNode.modifiers.add("public");
        }

        scope.symbols().functions().add(functionDeclarationNode);
    }

    public static void checkParameters(final List<ParameterNode> parameters, final Scope scope) {
        for (final ParameterNode parameter : parameters) {
            if (!TypeRecognize.findTypeDeclaration(parameter.type.name, scope)) {
                LoggerFacade.error("Unresolved symbol: '" + parameter.type + "'", parameter);
            }
        }
    }

    public static void loadBody(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        final TypeDeclarationNode containingType = scope.getContainingType();

        if (functionDeclarationNode.body == null) {
            return;
        }

        if (containingType != null && !functionDeclarationNode.modifiers.contains("static")) {
            addThisToSymbolTable(functionDeclarationNode.body.scope.symbols(), containingType.name);
        }

        BlockTraverse.traverse(functionDeclarationNode.body.blockNode, functionDeclarationNode.body.scope);

        if (!functionDeclarationNode.returnType.name.equals("Void")) {
            boolean haveReturn = false;
            for (final ASTNode node : functionDeclarationNode.body.blockNode.children) {
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

    private static void loadTypeParameters(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        for (final TypeParameterNode typeParameterNode : functionDeclarationNode.typeParameters) {
            final TypeDeclarationNode bound = TypeRecognize.getTypeDeclaration(typeParameterNode.bound.name, scope);
            if (bound == null) {
                LoggerFacade.error("Unresolved symbol: '" + typeParameterNode.bound.name + "'", functionDeclarationNode);
                return;
            }

            typeParameterNode.updateBound(bound);
            scope.symbols().typeParameters().add(typeParameterNode);
        }
    }
}
