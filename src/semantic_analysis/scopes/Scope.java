package semantic_analysis.scopes;

import parser.nodes.ASTNode;
import parser.nodes.classes.*;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.SymbolTable;

import java.util.Objects;

public record Scope (
    Scope parent,
    SymbolTable symbols,
    ASTNode currentParent,
    Type type
) {
    public boolean findSymbol(String symbol) {
        return findInterface(symbol) || findClass(symbol) || findFunction(symbol) || findField(symbol);
    }

    public ClassDeclarationNode getClass(String symbol) {
        ClassDeclarationNode declaration = symbols.getClass(symbol);

        return declaration != null ?
            declaration :
            parent != null ? parent.getClass(symbol) : null;
    }

    public InterfaceNode getInterface(String symbol) {
        InterfaceNode declaration = symbols.getInterface(symbol);

        return declaration != null ?
            declaration :
            parent != null ? parent.getInterface(symbol) : null;
    }

    public TypeDeclarationNode getTypeDeclaration(String symbol) {
        TypeDeclarationNode declaration = symbols.getTypeDeclaration(symbol);

        return declaration != null ?
            declaration :
            parent != null ? parent.getTypeDeclaration(symbol) : null;
    }

    public FunctionDeclarationNode getFunction(String symbol) {
        FunctionDeclarationNode declaration = symbols.getFunction(symbol);

        return declaration != null ?
            declaration :
            parent != null ? parent.getFunction(symbol) : null;
    }

    public FieldNode getField(String symbol) {
        FieldNode declaration = symbols.getField(symbol);

        return declaration != null ?
            declaration :
            parent != null ? parent.getField(symbol) : null;
    }

    public boolean isSameType(String type, String superType) {
        if (Objects.equals(type, superType)) {
            return true;
        }

        final ClassDeclarationNode classDeclarationNode = symbols.getClass(type);
        if (classDeclarationNode != null) {
            if (!classDeclarationNode.baseClasses.isEmpty() && classDeclarationNode.baseClasses.get(0).name.equals(superType)) {
                return true;
            }
            if (!classDeclarationNode.baseClasses.isEmpty() && isSameType(classDeclarationNode.baseClasses.get(0).name, superType)) {
                return true;
            }
        }

        final TypeDeclarationNode typeDeclarationNode = symbols.getTypeDeclaration(type);
        if (typeDeclarationNode != null) {
            for (final BaseInterfaceNode baseInterfaceNode : symbols.getTypeDeclaration(type).implementedInterfaces) {
                if (baseInterfaceNode.name.equals(superType) || isSameType(baseInterfaceNode.name, superType)) {
                    return true;
                }
            }
        }

        return parent != null && parent.isSameType(type, superType);
    }

    public boolean findClass(String symbol) {
        return symbols.findClass(symbol) || (parent != null && parent().findClass(symbol));
    }

    public boolean findInterface(String symbol) {
        return symbols.findInterface(symbol) || (parent != null && parent().findInterface(symbol));
    }

    public boolean findTypeDeclaration(String symbol) {
        return findClass(symbol) || findInterface(symbol);
    }

    public boolean findFunction(String symbol) {
        return symbols.findFunction(symbol) || (parent != null && parent().findFunction(symbol));
    }

    public boolean findField(String symbol) {
        return symbols.findField(symbol) || (parent != null && parent().findField(symbol));
    }

    public enum Type {
        TOP,
        CLASS,
        FUNCTION
    }
}