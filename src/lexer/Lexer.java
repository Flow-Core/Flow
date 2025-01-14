package lexer;

import lexer.token.Token;
import lexer.token.TokenType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static final EnumMap<TokenType, Pattern> patterns = new EnumMap<>(TokenType.class);
    private final List<Token> tokens;
    private final String code;
    private int currentLine;
    private int currentPosition;

    public Lexer(final String code) {
        this.code = code;
        tokens = new ArrayList<>();
        currentLine = 1;
        currentPosition = 0;
    }

    public List<Token> tokenize() throws RuntimeException {
        Token previousToken = null;
        while (currentPosition < code.length()) {
            char currentChar = code.charAt(currentPosition);

            if (Character.isWhitespace(currentChar) && currentChar != '\n') {
                currentPosition++;
                continue;
            }

            final Token token = nextToken();
            if (token == null) {
                throw new RuntimeException("Unexpected token at " + currentLine);
            }

            if (token.type() != TokenType.COMMENT) {
                if (!token.isLineTerminator() || (previousToken == null || !previousToken.isLineTerminator())) {
                    tokens.add(token);
                    previousToken = token;
                }
                if (token.type() == TokenType.NEW_LINE) {
                    currentLine++;
                }
            }
        }

        tokens.add(new Token(TokenType.EOF, "", currentLine));
        return tokens;
    }

    private Token nextToken() {
        for (final TokenType type : TokenType.values()) {
            final Pattern pattern = patterns.get(type);
            final Matcher matcher = pattern.matcher(code.substring(currentPosition));

            if (matcher.lookingAt()) {
                String value = matcher.group();
                currentPosition += value.length();

                if (type == TokenType.STRING) {
                    value = value.substring(1, value.length() - 1);
                }

                if (type == TokenType.STRING || type == TokenType.COMMENT) {
                    final Pattern newLinePattern = Pattern.compile("(\\r\\n|\\n|\\r)");
                    final Matcher newLineMatcher = newLinePattern.matcher(value);

                    while (newLineMatcher.find()) {
                        currentLine++;
                    }
                }

                return new Token(type, value, currentLine);
            }
        }

        return null;
    }

    static {
        patterns.put(TokenType.COMMENT, Pattern.compile("//.*|/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/"));

        // Keywords (must be checked before identifiers)
        patterns.put(TokenType.IF, Pattern.compile("if"));
        patterns.put(TokenType.ELSE, Pattern.compile("\\belse\\b"));
        patterns.put(TokenType.FOR, Pattern.compile("\\bfor\\b"));
        patterns.put(TokenType.FOREACH, Pattern.compile("\\bforeach\\b"));
        patterns.put(TokenType.WHILE, Pattern.compile("\\bwhile\\b"));
        patterns.put(TokenType.DO, Pattern.compile("\\do\\b"));
        patterns.put(TokenType.FUNC, Pattern.compile("\\b(func)\\b"));
        patterns.put(TokenType.CLASS, Pattern.compile("\\bclass\\b"));
        patterns.put(TokenType.INTERFACE, Pattern.compile("\\binterface\\b"));
        patterns.put(TokenType.CONSTRUCTOR, Pattern.compile("\\bconstructor\\b"));
        patterns.put(TokenType.INIT, Pattern.compile("\\binit\\b"));
        patterns.put(TokenType.TRY, Pattern.compile("\\btry\\b"));
        patterns.put(TokenType.CATCH, Pattern.compile("\\bcatch\\b"));

        patterns.put(TokenType.CONST, Pattern.compile("\\bconst\\b"));
        patterns.put(TokenType.VAL, Pattern.compile("\\bval\\b"));
        patterns.put(TokenType.VAR, Pattern.compile("\\bvar\\b"));

        patterns.put(TokenType.MODIFIER, Pattern.compile("\\b(private|" +
            "protected|" +
            "public|" +
            "static|" +
            "abstract|" +
            "final|" +
            "open|" +
            "data|" +
            "sealed" +
        ")\\b"));

        // Boolean literals
        patterns.put(TokenType.BOOLEAN, Pattern.compile("\\b(true|false)\\b"));

        // IP
        patterns.put(TokenType.IPV4, Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+"));
        patterns.put(TokenType.IPV6, Pattern.compile(
                "^(" +
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
                        ")"
        ));

        // Numbers
        patterns.put(TokenType.FLOAT, Pattern.compile("(^\\d+\\.\\d+f|^\\d+f|^\\.\\d+f)"));
        patterns.put(TokenType.DOUBLE, Pattern.compile("(\\d+\\.\\d+|\\.\\d+)"));
        patterns.put(TokenType.INT, Pattern.compile("^\\d+"));

        // Identifiers
        patterns.put(TokenType.IDENTIFIER, Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*"));

        // Strings
        patterns.put(TokenType.STRING, Pattern.compile("\"(\\\\.|[^\"])*\""));

        // Operators
        patterns.put(TokenType.OPERATOR, Pattern.compile("==|!=|<=|>=|<|>|\\+\\+|--|\\+|-|\\*|/|%|&&|\\|\\|"));
        patterns.put(TokenType.EQUAL_OPERATOR, Pattern.compile("="));
        patterns.put(TokenType.COLON_OPERATOR, Pattern.compile(":"));
        patterns.put(TokenType.DOT_OPERATOR, Pattern.compile("\\."));
        patterns.put(TokenType.COMMA, Pattern.compile(","));

        // Grouping and Bracketing
        patterns.put(TokenType.OPEN_PARENTHESES, Pattern.compile("\\("));
        patterns.put(TokenType.CLOSE_PARENTHESES, Pattern.compile("\\)"));
        patterns.put(TokenType.OPEN_BRACKETS, Pattern.compile("\\["));
        patterns.put(TokenType.CLOSE_BRACKETS, Pattern.compile("]"));
        patterns.put(TokenType.OPEN_BRACES, Pattern.compile("\\{"));
        patterns.put(TokenType.CLOSE_BRACES, Pattern.compile("}"));

        // New Line
        patterns.put(TokenType.NEW_LINE, Pattern.compile("(\\r\\n|\\n|\\r)"));
        patterns.put(TokenType.SEMICOLON, Pattern.compile(";"));
    }
}
