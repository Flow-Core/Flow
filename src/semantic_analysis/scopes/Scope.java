package semantic_analysis.scopes;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.visitors.ExpressionTraverse.TypeWrapper;

public record Scope (
    Scope parent,
    SymbolTable symbols,
    ASTNode currentParent,
    Type type
) {
    public String getFQName(ASTNode node) {
        if (symbols.bindingContext().containsKey(node)) {
            return symbols.bindingContext().get(node);
        }

        if (parent == null) {
            return null;
        }

        return parent.getFQName(node);
    }

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

    public boolean isSameType(TypeWrapper type, TypeWrapper superType) {
        return symbols().isSameType(type, superType) || parent != null && parent.isSameType(type, superType);
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

    public boolean findLocalVariable(String symbol) {
        return type == Type.FUNCTION && (symbols.findField(symbol) || (parent != null && parent().findLocalVariable(symbol)));
    }

    public TypeDeclarationNode getContainingType() {
        if (currentParent == null) return null;

        if (currentParent instanceof TypeDeclarationNode typeDeclaration)
            return typeDeclaration;

        return parent.getContainingType();
    }

    public enum Type {
        TOP,
        CLASS,
        FUNCTION
    }
}