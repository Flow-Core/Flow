package semantic_analysis;

import parser.nodes.ASTNode;
import parser.nodes.FunctionCall;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.VariableAssignment;
import parser.nodes.variable.VariableDeclaration;
import parser.nodes.variable.VariableReference;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SymbolCohesionCheck {
    public static List<FunctionDeclarationNode> performCheck(BlockNode root) throws SA_UnresolvedSymbolException {
        List<ASTNode> statements = root.getChildren();

        List<VariableDeclaration> variables = new ArrayList<>();

        List<FunctionDeclarationNode> functions = loadFunctions(root);

        for (ASTNode statement : statements) {
            if (statement instanceof FunctionCall functionCall) {
                if (functions.stream().noneMatch(
                        function -> Objects.equals(function.getName(), functionCall.getName())
                )) {
                    throw new SA_UnresolvedSymbolException("Function '" + functionCall.getName() + "' does not exist in the current context");
                }
            }
            if (statement instanceof VariableDeclaration declaration) {
                if (variables.stream().anyMatch(
                        variable -> Objects.equals(variable.getName(), declaration.getName())
                )) {
                    throw new SA_UnresolvedSymbolException("Variable '" + declaration.getName() + "' already exists in the current context");
                }

                variables.add(declaration);
            }
            if (statement instanceof VariableReference reference) {
                if (variables.stream().noneMatch(
                        variable -> Objects.equals(variable.getName(), reference.getVariable())
                )) {
                    throw new SA_UnresolvedSymbolException("Variable '" + reference.getVariable() + "' does not exist in the current context");
                }
            }
            if (statement instanceof VariableAssignment reference) {
                if (variables.stream().noneMatch(
                        variable -> Objects.equals(variable.getName(), reference.getVariable())
                )) {
                    throw new SA_UnresolvedSymbolException("Variable '" + reference.getVariable() + "' does not exist in the current context");
                }
            }
        }

        return functions;
    }

    public static List<FunctionDeclarationNode> loadFunctions(BlockNode root) {
        List<ASTNode> statements = root.getChildren();
        List<FunctionDeclarationNode> functions = new ArrayList<>();

        for (ASTNode statement : statements) {
            if (statement instanceof FunctionDeclarationNode declaration) {
                functions.add(declaration);
            }
        }

        return functions;
    }
}