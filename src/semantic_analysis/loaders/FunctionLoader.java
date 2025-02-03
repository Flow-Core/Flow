package semantic_analysis.loaders;

import parser.nodes.classes.FieldNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.scopes.Scope;
import semantic_analysis.visitors.BlockTraverse;

import java.util.ArrayList;

public class FunctionLoader {
    public static void load(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        if (!scope.findTypeDeclaration(functionDeclarationNode.returnType)) {
            throw new SA_UnresolvedSymbolException(functionDeclarationNode.returnType);
        }

        // Check and add parameters to the st
        final SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        for (final ParameterNode parameter : functionDeclarationNode.parameters) {
            if (!scope.findTypeDeclaration(parameter.type)) {
                throw new SA_UnresolvedSymbolException(parameter.type);
            }
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

        BlockTraverse.traverse(functionDeclarationNode.block, new Scope(scope, symbolTable, functionDeclarationNode, Scope.Type.FUNCTION));
    }
}
