package semantic_analysis;

import parser.nodes.*;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BlockNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SymbolCohesionCheck {
    public static void functionScopeCheck(BlockNode root, SymbolTable outerScopeSymbols) throws SA_UnresolvedSymbolException {
        List<ASTNode> statements = root.children();

        List<VariableDeclarationNode> variables = new ArrayList<>();

        List<String> allOuterSymbols = combineSymbols(outerScopeSymbols);

        for (ASTNode statement : statements) {
            if (statement instanceof VariableDeclarationNode declaration) {
                if (variables.stream().anyMatch(
                        variable -> Objects.equals(variable.name(), declaration.name())
                    ) ||
                    allOuterSymbols.stream().anyMatch(
                        symbol -> Objects.equals(symbol, declaration.name())
                    )
                ) {
                    throw new SA_UnresolvedSymbolException("Name '" + declaration.name() + "' already exists in the current context");
                }

                variables.add(declaration);
            }
            if (statement instanceof VariableAssignmentNode reference) {
                if (variables.stream().noneMatch(
                    variable -> Objects.equals(variable.name(), reference.variable())
                )) {
                    throw new SA_UnresolvedSymbolException("Variable '" + reference.variable() + "' does not exist in the current context");
                }
            }
            if (statement instanceof ExpressionNode expression) {

            }
        }
    }

    private static void checkExpression(SymbolTable scopeSymbols,
                                        List<VariableDeclarationNode> variables,
                                        ExpressionNode expression) {
        if (expression instanceof BinaryExpressionNode binary) {
            checkExpression(scopeSymbols, variables, binary.left());
            checkExpression(scopeSymbols, variables, binary.right());
        }
        if (expression instanceof VariableReferenceNode reference) {
            checkVariableReference(scopeSymbols, variables, reference);
        }
        if (expression instanceof FunctionCall functionCall) {
            checkFunctionCall(scopeSymbols, functionCall);
        } // TODO: Add unary expressions
    }

    private static void checkVariableReference(SymbolTable scopeSymbols,
                                               List<VariableDeclarationNode> variables,
                                               VariableReferenceNode referenceNode) {
        if (variables.stream().noneMatch(
                variable -> Objects.equals(variable.name(), referenceNode.variable())
            ) &&
            scopeSymbols.fields().stream().noneMatch(
                field -> Objects.equals(referenceNode.variable(), field.initialization().declaration().name())
            )
        ) {
            throw new SA_UnresolvedSymbolException("Variable '" + referenceNode.variable() + "' does not exist in the current context");
        }
    }

    private static void checkFunctionCall(SymbolTable scopeSymbols,
                                          FunctionCall functionCall) {
        if (scopeSymbols.functions().stream().noneMatch(
            function -> Objects.equals(function.name(), functionCall.name())
        )) {
            throw new SA_UnresolvedSymbolException("Function '" + functionCall.name() + "' does not exist in the current context");
        }
    }

    public static void classScopeCheck(BlockNode root) throws SA_UnresolvedSymbolException {
        SymbolTable symbolTable = loadSymbols(root);
        List<ASTNode> statements = root.children();

        for (ASTNode statement : statements) {
            if (statement instanceof ClassDeclarationNode declaration) {
                if (symbolTable.classes().stream().noneMatch(
                    classInfo -> Objects.equals(classInfo.name(), declaration.name())
                )) {
                    classScopeCheck(declaration.initBlock());
                } else {
                    throw new SA_UnresolvedSymbolException("Class '" + declaration.name() + "' does not exist in the current context");
                }
            }
        }
    }

    public static SymbolTable loadSymbols(BlockNode root) {
        List<ASTNode> statements = root.children();
        List<ClassDeclarationNode> classes = new ArrayList<>();
        List<FunctionDeclarationNode> functions = new ArrayList<>();
        List<FieldNode> fields = new ArrayList<>();

        for (ASTNode statement : statements) {
            if (statement instanceof ClassDeclarationNode declaration) {
                if (classes.stream().noneMatch(
                    classDeclaration -> Objects.equals(classDeclaration.name(), declaration.name())
                )) {
                    classes.add(declaration);
                }
            }
            if (statement instanceof FunctionDeclarationNode declaration) {
                if (functions.stream().noneMatch(
                    function -> Objects.equals(function.name(), declaration.name())
                )) {
                    functions.add(declaration);
                }
            }
            if (statement instanceof FieldNode field) {
                if (fields.stream().noneMatch(
                    fieldInfo -> Objects.equals(
                        fieldInfo.initialization().declaration().name(),
                        field.initialization().declaration().name())
                )) {
                    fields.add(field);
                }
            }
        }

        return new SymbolTable(classes, functions, fields);
    }

    private static List<String> combineSymbols(SymbolTable table) {
        List<String> symbols = new ArrayList<>();

        symbols.addAll(table.classes().stream().map(
            ClassDeclarationNode::name
        ).toList());

        symbols.addAll(table.functions().stream().map(
            FunctionDeclarationNode::name
        ).toList());

        symbols.addAll(table.fields().stream().map(
            field -> field.initialization().declaration().name()
        ).toList());

        return symbols;
    }
}