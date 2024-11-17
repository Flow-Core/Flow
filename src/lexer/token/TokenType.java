package lexer.token;

public enum TokenType {
    COMMENT,

    IF,
    ELSE,
    FOR,
    FOREACH,
    WHILE,
    DO,
    FUNC,
    CLASS,
    INTERFACE,
    FINAL,
    CONST,

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
    EQUAL_OPERATOR,
    COLON_OPERATOR,
    DOT_OPERATOR,

    NEW_LINE,
}
