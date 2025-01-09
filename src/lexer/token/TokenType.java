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
    CONSTRUCTOR,
    INIT,
    TRY,
    CATCH,

    CONST,
    VAR,
    VAL,

    MODIFIER,

    OPERATOR,
    OPEN_PARENTHESES,
    CLOSE_PARENTHESES,
    OPEN_BRACKETS,
    CLOSE_BRACKETS,
    OPEN_BRACES,
    CLOSE_BRACES,

    IPV6,
    IPV4,
    FLOAT,
    DOUBLE,
    INT,
    STRING,
    BOOLEAN,

    IDENTIFIER,
    EQUAL_OPERATOR,
    COLON_OPERATOR,
    DOT_OPERATOR,
    COMMA,

    NEW_LINE
}
