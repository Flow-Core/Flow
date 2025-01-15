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
    FUNC("\\bfunc\\b"),
    CLASS("\\bclass\\b"),
    INTERFACE("\\binterface\\b"),
    TRY("\\btry\\b"),
    CATCH("\\bcatch\\b"),

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
    IS("\\bis\\b"),
    AS("\\bas\\b"),
    NULL("\\bnull\\b"),
    NEW("\\bnew\\b"),

    INIT("\\binit\\b"),
    CONSTRUCTOR("\\bconstructor\\b"),

    // Boolean literals
    BOOLEAN("\\b(true|false)\\b"),

    // IP
    IPV6("^(" +
        "([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
        "([0-9a-fA-F]{1,4}:){1,7}:|" +
        "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
        "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
        "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
        "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
        "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
        "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
        ":((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
        "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|" +
        ":((ffff(:0{1,4})?:)?(([0-9]{1,3}\\.){3}[0-9]{1,3}))|" +
        "([0-9a-fA-F]{1,4}:){1,4}:(([0-9]{1,3}\\.){3}[0-9]{1,3})" +
        ")"),
    IPV4("^\\d+\\.\\d+\\.\\d+\\.\\d+"),

    // Numbers
    FLOAT("(^\\d+\\.\\d+f|^\\d+f|^\\.\\d+f)"),
    DOUBLE("(\\d+\\.\\d+|\\.\\d+)"),
    INT("^\\d+"),

    // Identifiers
    IDENTIFIER("[a-zA-Z_][a-zA-Z0-9_]*"),

    // Strings
    STRING("\"(\\\\.|[^\"])*\""),

    // Operators
    OPERATOR("==|!=|<=|>=|<|>|\\+\\+|--|\\+|-|\\*|/|%|&&|\\|\\|"),
    EQUAL_OPERATOR("="),
    COLON_OPERATOR(":"),
    DOT_OPERATOR("\\."),
    COMMA(","),

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
