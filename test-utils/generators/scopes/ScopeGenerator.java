package generators.scopes;

import generators.ast.classes.ClassNodeGenerator;
import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.scopes.Scope;

public class ScopeGenerator {
    private final ClassDeclarationNode containingType = ClassNodeGenerator.builder().name("fileFl").build();
    private Scope parent = new Scope(null, SymbolTableGenerator.builder().build(), null, Scope.Type.TOP);
    private SymbolTable symbols = SymbolTable.getEmptySymbolTable();
    private ASTNode currentParent = containingType;
    private Scope.Type type = Scope.Type.TOP;

    public static ScopeGenerator builder() {
        return new ScopeGenerator();
    }

    public ScopeGenerator parent(Scope parent) {
        this.parent = parent;
        return this;
    }

    public ScopeGenerator symbols(SymbolTable symbols) {
        this.symbols = symbols;
        return this;
    }

    public ScopeGenerator currentParent(ASTNode currentParent) {
        this.currentParent = currentParent;
        return this;
    }

    public ScopeGenerator type(Scope.Type type) {
        this.type = type;
        return this;
    }

    public Scope build() {
        return new Scope(parent, symbols, currentParent, type);
    }
}
