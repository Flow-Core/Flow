package lexer.token;

public record Token(TokenType type, String value, int line) {

    public boolean isLineTerminator() {
        return type == TokenType.SEMICOLON || type == TokenType.NEW_LINE;
    }

    @Override
    public String toString() {
        if (type != TokenType.NEW_LINE) {
            return "Token(" + type + ", " + value + ", " + line + ")";
        } else {
            return "Token(" + type + ")";
        }
    }
}
