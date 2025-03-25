package semantic_analysis.visitors;

import logger.LoggerFacade;
import parser.nodes.FlowType;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.loaders.FunctionLoader;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableDeclarationNode;
import semantic_analysis.loaders.VariableLoader;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ClassTraverse {
    public static void loadMethodSignatures(TypeDeclarationNode typeDeclaration, Scope scope, boolean isInterface) {
        for (final FunctionDeclarationNode method : typeDeclaration.methods) {
            FunctionLoader.loadSignature(method, scope, isInterface, false);
        }
    }

    public static void loadFields(ClassDeclarationNode classDeclaration, Scope scope) {
        for (final FieldNode field : classDeclaration.fields) {
            VariableLoader.loadDeclaration(field, scope);

            if (field.initialization.assignment != null) {
                field.isInitialized = false;
            }
        }
    }

    public static void loadMethodBodies(ClassDeclarationNode classDeclaration, Scope scope) {
        final List<FunctionDeclarationNode> methods = new ArrayList<>(classDeclaration.methods);
        for (final FunctionDeclarationNode method : methods) {
            FunctionLoader.loadBody(method, scope);
        }
    }

    public static void loadConstructors(ClassDeclarationNode classDeclaration, Scope scope) {
        for (final ConstructorNode constructorNode : classDeclaration.constructors) {
            FunctionLoader.checkParameters(constructorNode.parameters, scope);

            final SymbolTable symbolTable = FunctionLoader.loadParameters(constructorNode.parameters);

            if (constructorNode.body == null) {
                LoggerFacade.error("Constructor must have a body", constructorNode);
            }

            addThisToSymbolTable(symbolTable, classDeclaration.name);

            final Scope bodyScope = new Scope(scope, symbolTable, classDeclaration, Scope.Type.FUNCTION);
            BlockTraverse.traverse(constructorNode.body.blockNode, bodyScope);
            constructorNode.body.scope = bodyScope;
        }
    }

    public static void addThisToSymbolTable(SymbolTable symbolTable, String type) {
        symbolTable.fields().add(
            new FieldNode(
                List.of(),
                new InitializedVariableNode(
                    new VariableDeclarationNode(
                        "val",
                        new FlowType(
                            type,
                            false,
                            false
                        ),
                        "this"
                    ),
                    null
                )
            )
        );
    }
}
