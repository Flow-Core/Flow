package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.InterfaceNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_RedefinitionException;

public class ClassSignatureVisitor implements ASTVisitor<SymbolTable> {
    @Override
    public void visit(final ASTNode node, final SymbolTable data) {
        if (node instanceof ClassDeclarationNode) {
            handleClass((ClassDeclarationNode) node, data);
        } else if (node instanceof InterfaceNode) {
            handleInterface((InterfaceNode) node, data);
        }
    }

    private void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable data) {
        if (data.classes().stream().anyMatch(currentClass -> currentClass.name.equals(classDeclaration.name))) {
            throw new SA_RedefinitionException(classDeclaration.name);
        }

        data.classes().add(classDeclaration);
    }

    private void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable data) {
        if (data.classes().stream().anyMatch(currentClass -> currentClass.name.equals(interfaceDeclaration.name))) {
            throw new SA_RedefinitionException(interfaceDeclaration.name);
        }

        data.interfaces().add(interfaceDeclaration);
    }
}