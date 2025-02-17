package lexer;

import lexer.token.Token;
import lexer.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
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
            if (type.getRegex() == null) {
                continue;
            }

            final Pattern pattern = Pattern.compile(type.getRegex());
            final Matcher matcher = pattern.matcher(code.substring(currentPosition));

            if (matcher.lookingAt()) {
                String value = matcher.group();
                currentPosition += value.length();

                if (type == TokenType.STRING || type == TokenType.CHAR) {
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
}
