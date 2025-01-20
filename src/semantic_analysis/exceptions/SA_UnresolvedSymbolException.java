package semantic_analysis.exceptions;

public class SA_UnresolvedSymbolException extends RuntimeException {
    public SA_UnresolvedSymbolException(String symbol) {
        super(
            "Unresolved symbol: '" + symbol + "'"
        );
    }
}
