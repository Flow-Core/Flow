package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.InterfaceNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_RedefinitionException;

public class ClassSignatureVisitor implements ASTVisitor<SymbolTable> {
    private final SymbolTable packageLevel;

    public ClassSignatureVisitor(SymbolTable packageLevel) {
        this.packageLevel = packageLevel;
    }

    @Override
    public void visit(final ASTNode node, final SymbolTable fileLevel) {
        if (node instanceof ClassDeclarationNode) {
            handleClass((ClassDeclarationNode) node, fileLevel);
        } else if (node instanceof InterfaceNode) {
            handleInterface((InterfaceNode) node, fileLevel);
        }
    }

    private void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable fileLevel) {
        boolean isPublic = classDeclaration.modifiers.contains("public") || classDeclaration.modifiers.isEmpty();

        if (isPublic) {
            if (packageLevel.classes().stream().anyMatch(currentClass -> currentClass.name.equals(classDeclaration.name))) {
                throw new SA_RedefinitionException(classDeclaration.name);
            }
            packageLevel.classes().add(classDeclaration);
        } else {
            if (fileLevel.classes().stream().anyMatch(currentClass -> currentClass.name.equals(classDeclaration.name))) {
                throw new SA_RedefinitionException(classDeclaration.name);
            }
            fileLevel.classes().add(classDeclaration);
        }
    }

    private void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable fileLevel) {
        boolean isPublic = interfaceDeclaration.modifiers.contains("public") || interfaceDeclaration.modifiers.isEmpty();

        if (isPublic) {
            if (packageLevel.interfaces().stream().anyMatch(currentInterface -> currentInterface.name.equals(interfaceDeclaration.name))) {
                throw new SA_RedefinitionException(interfaceDeclaration.name);
            }
            packageLevel.interfaces().add(interfaceDeclaration);
        } else {
            if (fileLevel.interfaces().stream().anyMatch(currentInterface -> currentInterface.name.equals(interfaceDeclaration.name))) {
                throw new SA_RedefinitionException(interfaceDeclaration.name);
            }
            fileLevel.interfaces().add(interfaceDeclaration);
        }
    }
}