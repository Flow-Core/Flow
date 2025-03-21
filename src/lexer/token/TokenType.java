package lexer.token;

public enum TokenType {
    // Comments
    COMMENT("//.*|/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/"),

    // Keywords
    IF("\\bif\\b"),
    ELSE("\\belse\\b"),
    FOR("\\bfor\\b"),
    FOREACH("\\bforeach\\b"),
    WHILE("\\bwhile\\b"),
    DO("\\bdo\\b"),
    SWITCH("\\bswitch\\b"),
    CASE("\\bcase\\b"),
    DEFAULT("\\bdefault\\b"),
    FUNC("\\bfunc\\b"),
    CLASS("\\bclass\\b"),
    INTERFACE("\\binterface\\b"),
    SERVER("\\bserver\\b"),
    TRY("\\btry\\b"),
    CATCH("\\bcatch\\b"),
    FINALLY("\\bfinally\\b"),
    THROW("\\bthrow\\b"),

    CONST("\\bconst\\b"),
    VAL("\\bval\\b"),
    VAR("\\bvar\\b"),

    MODIFIER("\\b(private|protected|public|static|abstract|final|open|data|sealed|override)\\b"),

    PACKAGE("\\bpackage\\b"),
    IMPORT("\\bimport\\b"),

    RETURN("\\breturn\\b"),
    BREAK("\\bbreak\\b"),
    CONTINUE("\\bcontinue\\b"),

    IN("\\bin\\b"),
    IS_NOT("\\bis not\\b"),
    IS("\\bis\\b"),
    AS("\\bas\\b"),
    NULL("\\bnull\\b"),
    NEW("\\bnew\\b"),

    INIT("\\binit\\b"),
    CONSTRUCTOR("\\bconstructor\\b"),

    // Boolean literals
    BOOLEAN("\\b(true|false)\\b"),

    // IP
    IPV6("([0-9a-fA-F]+:){7}[0-9a-fA-F]+"),
    IPV4("^\\d+\\.\\d+\\.\\d+\\.\\d+"),

    // Numbers
    LONG("^\\d+[lL]"),
    FLOAT("(^\\d+\\.\\d+f|^\\d+f|^\\.\\d+f)"),
    DOUBLE("(\\d+\\.\\d+|\\.\\d+)"),
    INT("^\\d+"),

    // Identifiers
    IDENTIFIER("[a-zA-Z_][a-zA-Z0-9_]*"),

    // Strings and chars
    STRING("\"(\\\\.|[^\"])*\""),
    CHAR("'(\\\\.|[^'\\\\])'"),

    // Operators
    ARROW_OPERATOR("~+>+|-+>+|=+>+|-+=+>+|~+=+>+|0=+\\[}:::+>+|\\|+>+|>>>>+"),
    ASSIGNMENT_OPERATOR("\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>="),
    UNARY_OPERATOR("!!|!|\\+\\+|--"),
    POLARITY_OPERATOR("\\+|-"),
    BINARY_OPERATOR("==|!=|<=|>=|<|>|\\*|/|%|&&|\\|\\|"),
    EQUAL_OPERATOR("="),
    CONNECTION_OPERATOR("~"),
    REFERENCE_OPERATOR("::"),
    COLON_OPERATOR(":"),
    SAFE_CALL("\\?\\."),
    NOT_NULL_ASSERTION("!!"),
    DOT_OPERATOR("\\."),
    COMMA(","),
    NULLABLE("\\?"),

    // Grouping and Bracketing
    OPEN_PARENTHESES("\\("),
    CLOSE_PARENTHESES("\\)"),
    OPEN_BRACKETS("\\["),
    CLOSE_BRACKETS("]"),
    OPEN_BRACES("\\{"),
    CLOSE_BRACES("\\}"),

    // New Line
    NEW_LINE("(\\r\\n|\\n|\\r)"),
    SEMICOLON(";"),

    EOF(null);

    private final String regex;

    TokenType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
