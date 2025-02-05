package generators.scopes;

import parser.nodes.ASTNode;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.scopes.Scope;

public class ScopeGenerator {
    private Scope parent = null;
    private SymbolTable symbols = SymbolTable.getEmptySymbolTable();
    private ASTNode currentParent = null;
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
