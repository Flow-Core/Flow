package semantic_analysis;

import parser.nodes.ASTNode;
import parser.nodes.FunctionCall;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SymbolCohesionCheck {
    public static List<FunctionDeclarationNode> performCheck(BlockNode root) throws SA_UnresolvedSymbolException {
        List<ASTNode> statements = root.children();

        List<VariableDeclarationNode> variables = new ArrayList<>();

        List<FunctionDeclarationNode> functions = loadFunctions(root);

        for (ASTNode statement : statements) {
            if (statement instanceof FunctionCall functionCall) {
                if (functions.stream().noneMatch(
                        function -> Objects.equals(function.name(), functionCall.name())
                )) {
                    throw new SA_UnresolvedSymbolException("Function '" + functionCall.name() + "' does not exist in the current context");
                }
            }
            if (statement instanceof VariableDeclarationNode declaration) {
                if (variables.stream().anyMatch(
                        variable -> Objects.equals(variable.name(), declaration.name())
                )) {
                    throw new SA_UnresolvedSymbolException("Variable '" + declaration.name() + "' already exists in the current context");
                }

                variables.add(declaration);
            }
            if (statement instanceof VariableReferenceNode reference) {
                if (variables.stream().noneMatch(
                        variable -> Objects.equals(variable.name(), reference.variable())
                )) {
                    throw new SA_UnresolvedSymbolException("Variable '" + reference.variable() + "' does not exist in the current context");
                }
            }
            if (statement instanceof VariableAssignmentNode reference) {
                if (variables.stream().noneMatch(
                        variable -> Objects.equals(variable.name(), reference.variable())
                )) {
                    throw new SA_UnresolvedSymbolException("Variable '" + reference.variable() + "' does not exist in the current context");
                }
            }
        }

        return functions;
    }

    public static List<FunctionDeclarationNode> loadFunctions(BlockNode root) {
        List<ASTNode> statements = root.children();
        List<FunctionDeclarationNode> functions = new ArrayList<>();

        for (ASTNode statement : statements) {
            if (statement instanceof FunctionDeclarationNode declaration) {
                functions.add(declaration);
            }
        }

        return functions;
    }
}