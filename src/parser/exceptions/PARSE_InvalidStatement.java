package parser.exceptions;

public class PARSE_InvalidStatement extends RuntimeException {
    public PARSE_InvalidStatement(String message) {
        super(message);
    }
}
