package semantic_analysis.visitors;

import logger.Logger;
import logger.LoggerFacade;
import parser.ASTMetaDataStore;
import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.Scope;
import semantic_analysis.SymbolTable;

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
        SymbolTable currSymbols = SymbolTable.getEmptySymbolTable();

        for (ASTNode node : root.children) {
            if (node instanceof ClassDeclarationNode classDeclaration) {
                if (currSymbols.findSymbol(classDeclaration.name))
                    LoggerFacade.getLogger().log(
                        Logger.Severity.ERROR,
                        "Symbol '" + classDeclaration.name + "' redefined",
                        ASTMetaDataStore.getInstance().getLine(classDeclaration),
                        ASTMetaDataStore.getInstance().getFile(classDeclaration)
                    );
                else
                    currSymbols.classes().add(classDeclaration);
            }
            if (node instanceof FunctionDeclarationNode functionDeclaration) {
                if (currSymbols.findSymbol(functionDeclaration.name))
                    LoggerFacade.getLogger().log(
                        Logger.Severity.ERROR,
                        "Symbol '" + functionDeclaration.name + "' redefined",
                        ASTMetaDataStore.getInstance().getLine(functionDeclaration),
                        ASTMetaDataStore.getInstance().getFile(functionDeclaration)
                    );
                else
                    currSymbols.functions().add(functionDeclaration);
            }
            if (node instanceof FieldNode fieldDeclaration) {
                if (currSymbols.findSymbol(fieldDeclaration.initialization.declaration.name))
                    LoggerFacade.getLogger().log(
                        Logger.Severity.ERROR,
                        "Symbol '" + fieldDeclaration.initialization.declaration.name + "' redefined",
                        ASTMetaDataStore.getInstance().getLine(fieldDeclaration.initialization),
                        ASTMetaDataStore.getInstance().getFile(fieldDeclaration.initialization)
                    );
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
}
