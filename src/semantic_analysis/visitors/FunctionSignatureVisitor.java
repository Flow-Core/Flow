package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.ReturnStatementNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.scopes.Scope;

import java.util.ArrayList;

public class FunctionSignatureVisitor implements ASTVisitor<Scope> {
    @Override
    public void visit(ASTNode node, Scope data) {
        if (node instanceof FunctionDeclarationNode functionDeclarationNode) {
            loadSignature(functionDeclarationNode, data);
        }
    }

    private void loadSignature(final FunctionDeclarationNode functionDeclarationNode, final Scope scope) {
        if (!scope.findTypeDeclaration(functionDeclarationNode.returnType)) {
            throw new SA_UnresolvedSymbolException(functionDeclarationNode.returnType);
        }

        for (final ParameterNode parameter : functionDeclarationNode.parameters) {
            if (!scope.findTypeDeclaration(parameter.type)) {
                throw new SA_UnresolvedSymbolException(parameter.type);
            }
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
}
