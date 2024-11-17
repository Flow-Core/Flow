package lexer.token;

public enum TokenType {
    COMMENT,

    IF,
    ELSE,
    FOR,
    WHILE,
    FUNC,
    CLASS,

    OPERATOR,
    OPEN_PARENTHESES,
    CLOSE_PARENTHESES,
    OPEN_BRACKETS,
    CLOSE_BRACKETS,
    OPEN_BRACES,
    CLOSE_BRACES,

    NUMBER,
    STRING,
    BOOLEAN,

    IDENTIFIER,

    NEW_LINE,
}
