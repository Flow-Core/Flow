package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.classes.BaseInterfaceNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;

public class ClassLoaderVisitor implements ASTVisitor<SymbolTable> {
    @Override
    public void visit(final ASTNode node, final SymbolTable data) {
        if (node instanceof ClassDeclarationNode classDeclaration) {
            handleClass(classDeclaration, data);
        } else if (node instanceof InterfaceNode interfaceDeclaration) {

        } else if (node instanceof FieldNode field) {

        } else if (node instanceof FunctionDeclarationNode functionDeclaration) {

        }
    }

    private void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable data) {
        if (!classDeclaration.baseClasses.isEmpty()) {
            if (classDeclaration.baseClasses.size() > 1) {
                throw new SA_SemanticError("Only one class can appear in a supertype list");
            }
            if (!data.findClass(classDeclaration.baseClasses.get(0).name)) {
                throw new SA_UnresolvedSymbolException(classDeclaration.baseClasses.get(0).name);
            }
        }
        for (final BaseInterfaceNode interfaceNode : classDeclaration.interfaces) {
            if (!data.findInterface(interfaceNode.name)) {
                throw new SA_UnresolvedSymbolException(interfaceNode.name);
            }
        }
    }
}
