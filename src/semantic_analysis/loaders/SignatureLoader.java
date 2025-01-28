package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.PackageWrapper;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_RedefinitionException;

import java.util.List;

import static semantic_analysis.SymbolTable.joinPath;

public class SignatureLoader {
    public static void load(final List<ASTNode> nodes, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        for (final ASTNode node : nodes) {
            if (node instanceof ClassDeclarationNode classDeclarationNode) {
                handleClass(classDeclarationNode, fileLevel, packageWrapper);
            } else if (node instanceof InterfaceNode interfaceNode) {
                handleInterface(interfaceNode, fileLevel, packageWrapper);
            } else if (node instanceof FunctionDeclarationNode functionDeclarationNode) {
                handleFunction(functionDeclarationNode, fileLevel, packageWrapper);
            } else if (node instanceof FieldNode fieldNode) {
                handleField(fieldNode, fileLevel, packageWrapper);
            }
        }
    }

    private static void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = !classDeclaration.modifiers.contains("private") && !classDeclaration.modifiers.contains("protected");

        if (packageWrapper.symbolTable().findSymbol(classDeclaration.name)) {
            throw new SA_RedefinitionException(classDeclaration.name);
        }

        if (isPublic) {
            packageWrapper.symbolTable().classes().add(classDeclaration);
            packageWrapper.symbolTable().bindingContext().put(classDeclaration, joinPath(packageWrapper.path(), classDeclaration.name));
        } else {
            fileLevel.classes().add(classDeclaration);
        }
    }

    private static void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = !interfaceDeclaration.modifiers.contains("private") && !interfaceDeclaration.modifiers.contains("protected");

        if (packageWrapper.symbolTable().findSymbol(interfaceDeclaration.name)) {
            throw new SA_RedefinitionException(interfaceDeclaration.name);
        }

        if (isPublic) {
            packageWrapper.symbolTable().interfaces().add(interfaceDeclaration);
            packageWrapper.symbolTable().bindingContext().put(interfaceDeclaration, joinPath(packageWrapper.path(), interfaceDeclaration.name));
        } else {
            fileLevel.interfaces().add(interfaceDeclaration);
        }
    }

    private static void handleFunction(final FunctionDeclarationNode functionDeclarationNode, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = !functionDeclarationNode.modifiers.contains("private") && !functionDeclarationNode.modifiers.contains("protected");

        if (isPublic) {
            packageWrapper.symbolTable().functions().add(functionDeclarationNode);
            packageWrapper.symbolTable().bindingContext().put(functionDeclarationNode, joinPath(packageWrapper.path(), functionDeclarationNode.name));
        } else {
            fileLevel.functions().add(functionDeclarationNode);
        }
    }

    private static void handleField(final FieldNode fieldNode, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = !fieldNode.modifiers.contains("private") && !fieldNode.modifiers.contains("protected");

        final String name = fieldNode.initialization.declaration.name;
        if (packageWrapper.symbolTable().findSymbol(name)) {
            throw new SA_RedefinitionException(name);
        }

        if (isPublic) {
            packageWrapper.symbolTable().fields().add(fieldNode);
            packageWrapper.symbolTable().bindingContext().put(fieldNode, joinPath(packageWrapper.path(), fieldNode.initialization.declaration.name));
        } else {
            fileLevel.fields().add(fieldNode);
        }
    }
}