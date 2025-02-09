package semantic_analysis.visitors;

import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.loaders.FunctionLoader;
import semantic_analysis.loaders.VariableLoader;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

public class ClassTraverse {
    public static void loadMethodSignatures(TypeDeclarationNode typeDeclaration, Scope scope) {
        for (final FunctionDeclarationNode method : typeDeclaration.methods) {
            FunctionLoader.loadSignature(method, scope);
        }
    }

    public static void loadFields(ClassDeclarationNode classDeclaration, Scope scope) {
        for (final FieldNode field : classDeclaration.fields) {
            VariableLoader.loadDeclaration(field, scope);
        }
    }

    public static void loadMethodBodies(ClassDeclarationNode classDeclaration, Scope scope) {
        for (final FunctionDeclarationNode method : classDeclaration.methods) {
            FunctionLoader.loadBody(method, scope);
        }
    }

    public static void loadConstructors(ClassDeclarationNode classDeclaration, Scope scope) {
        for (final ConstructorNode constructorNode : classDeclaration.constructors) {
            FunctionLoader.checkParameters(constructorNode.parameters, scope);

            final SymbolTable symbolTable = FunctionLoader.loadParameters(constructorNode.parameters);

            if (constructorNode.body == null) {
                throw new SA_SemanticError("Constructor should have a body");
            }

            BlockTraverse.traverse(constructorNode.body, new Scope(scope, symbolTable, classDeclaration, Scope.Type.FUNCTION));
        }
    }
}
