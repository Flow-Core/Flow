package semantic_analysis.exceptions;

public class SA_RedefinitionException extends RuntimeException {
    public SA_RedefinitionException(String symbol) {
        super(
            "Symbol '" + symbol + "' redefined"
        );
    }
}
