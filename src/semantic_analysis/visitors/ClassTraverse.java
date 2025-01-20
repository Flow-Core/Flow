package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.ExpressionBaseNode;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.Scope;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_RedefinitionException;

import java.util.ArrayList;
import java.util.List;

public class ClassTraverse {
    // TODO: imports

    public static void traverseClass(ClassDeclarationNode classDeclaration, Scope parent) {
        SymbolTable symbols = traverseBlock(classDeclaration.initBlock, parent);

        List<ParameterNode> primaryParameters = new ArrayList<>();
        List<ASTNode> assignments = new ArrayList<>();

        for (FieldNode field : classDeclaration.primaryConstructor) {
            primaryParameters.add(
                new ParameterNode(
                    field.initialization.declaration.type,
                    "_" + field.initialization.declaration.name,
                    null
                )
            );

            classDeclaration.fields.add(0, field);
            assignments.add(
                new VariableAssignmentNode(
                    field.initialization.declaration.name,
                    "=",
                    new ExpressionBaseNode(
                        new VariableReferenceNode("_" + field.initialization.declaration.name)
                    )
                )
            );
        }

        classDeclaration.constructors.add(
            new ConstructorNode(
                "public",
                primaryParameters,
                new BlockNode(
                    assignments
                )
            )
        );
    }

    public static SymbolTable traverseBlock(BlockNode root, Scope parent) {
        SymbolTable currSymbols = new SymbolTable(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        for (ASTNode node : root.children) {
            if (node instanceof ClassDeclarationNode classDeclaration) {
                if (findSymbol(currSymbols, classDeclaration.name))
                    throw new SA_RedefinitionException(classDeclaration.name);
                else
                    currSymbols.classes().add(classDeclaration);
            }
            if (node instanceof FunctionDeclarationNode functionDeclaration) {
                if (findSymbol(currSymbols, functionDeclaration.name))
                    throw new SA_RedefinitionException(functionDeclaration.name);
                else
                    currSymbols.functions().add(functionDeclaration);
            }
            if (node instanceof FieldNode fieldDeclaration) {
                if (findSymbol(currSymbols, fieldDeclaration.initialization.declaration.name))
                    throw new SA_RedefinitionException(fieldDeclaration.initialization.declaration.name);
                else
                    currSymbols.fields().add(fieldDeclaration);
            }
        }

        Scope scope = new Scope(parent, currSymbols);

        for (ClassDeclarationNode classDeclaration : currSymbols.classes()) {
            traverseClass(classDeclaration, scope);
        }

        return currSymbols;
    }

    public static boolean findSymbol(SymbolTable symbols, String symbol) {
        return symbols.classes().stream().anyMatch(
                existingClass -> existingClass.name.equals(symbol)
            ) ||
            symbols.functions().stream().anyMatch(
                existingFunction -> existingFunction.name.equals(symbol)
            ) ||
            symbols.fields().stream().anyMatch(
                existingField -> existingField.initialization.declaration.name.equals(symbol)
            );
    }
}
