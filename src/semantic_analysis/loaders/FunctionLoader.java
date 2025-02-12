package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.ReturnStatementNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.visitors.BlockTraverse;
import semantic_analysis.visitors.ExpressionTraverse;

import java.util.ArrayList;
import java.util.List;

import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;

public class FunctionLoader {
    public static void loadSignature(final FunctionDeclarationNode functionDeclarationNode, final Scope scope, boolean isInterface) {
        ModifierLoader.load(
            functionDeclarationNode.modifiers,
            isInterface ?
                ModifierLoader.ModifierType.FUNCTION_INTERFACE
                : ModifierLoader.ModifierType.FUNCTION
        );

        if (!scope.findTypeDeclaration(functionDeclarationNode.returnType)) {
            throw new SA_UnresolvedSymbolException(functionDeclarationNode.returnType);
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
            throw new SA_SemanticError("Conflicting overloads: ");
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
            if (!scope.findTypeDeclaration(parameter.type)) {
                throw new SA_UnresolvedSymbolException(parameter.type);
            }
        }
    }

    public static void loadBody(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        final SymbolTable symbolTable = loadParameters(functionDeclarationNode.parameters);

        if (functionDeclarationNode.block == null) {
            return;
        }

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
                throw new SA_SemanticError("Missing return statement");
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
                            parameter.name,
                            parameter.isNullable
                        ),
                        null
                    )
                )
            );
        }

        return symbolTable;
    }
}
