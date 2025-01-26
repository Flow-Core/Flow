package semantic_analysis.exceptions;

public class SA_RedefinitionException extends SA_SemanticError {
    public SA_RedefinitionException(String symbol) {
        super("Symbol '" + symbol + "' redefined");
    }
}
