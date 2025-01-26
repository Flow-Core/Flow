package semantic_analysis.exceptions;

public class SA_UnresolvedPackageException extends SA_SemanticError {
    public SA_UnresolvedPackageException(String symbol) {
        super("Package not found: '" + symbol + "'");
    }
}
