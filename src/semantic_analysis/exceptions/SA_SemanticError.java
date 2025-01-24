package semantic_analysis.exceptions;

public class SA_SemanticError extends RuntimeException {
    public SA_SemanticError(String message) {
        super(message);
    }
}