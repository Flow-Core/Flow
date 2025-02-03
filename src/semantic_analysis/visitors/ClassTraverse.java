package semantic_analysis.visitors;

import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.loaders.FunctionLoader;
import semantic_analysis.loaders.VariableLoader;
import semantic_analysis.scopes.Scope;

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
}
