package semantic_analysis.exceptions;

public class SA_UnresolvedSymbolException extends SA_SemanticError {
    public SA_UnresolvedSymbolException(String symbol) {
        super("Unresolved symbol: '" + symbol + "'");
    }
}
