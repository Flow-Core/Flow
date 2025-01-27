package parser.exceptions;

public class PARSE_UnexpectedToken extends RuntimeException {
    public PARSE_UnexpectedToken(String message) {
        super("Unexpected token: '" + message + "'");
    }
}
