package lexer;

import lexer.token.Token;
import lexer.token.TokenType;
import logger.LoggerFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final List<Token> tokens;
    private final String file;
    private final String code;
    private int currentLine;
    private int currentPosition;

    public Lexer(final String code, final String file) {
        this.code = code;
        this.file = file;

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
                throw LoggerFacade.getLogger().panic("Unexpected token", currentLine, file);
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

                    if (type == TokenType.STRING) {
                        value = unescape(value);
                    }
                }
                if ((type == TokenType.FLOAT || type == TokenType.DOUBLE || type == TokenType.LONG) && !Character.isDigit(value.charAt(value.length() - 1))) {
                    value = value.substring(0, value.length() - 1);
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

    private String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                if (i + 1 < s.length()) {
                    char next = s.charAt(++i);
                    switch (next) {
                        case 'n':
                            sb.append('\n');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '"':
                            sb.append('"');
                            break;
                        case '\'':
                            sb.append('\'');
                            break;
                        case 'u':
                            if (i + 4 < s.length()) {
                                String hex = s.substring(i + 1, i + 5);
                                try {
                                    int code = Integer.parseInt(hex, 16);
                                    sb.append((char) code);
                                } catch (NumberFormatException e) {
                                    throw LoggerFacade.getLogger().panic("Invalid Unicode escape sequence: \\u" + hex, currentLine, file);
                                }
                                i += 4;
                            } else {
                                throw LoggerFacade.getLogger().panic("Incomplete Unicode escape sequence. Expected 4 hex digits after \\u.", currentLine, file);
                            }
                            break;
                        default:
                            throw LoggerFacade.getLogger().panic("Invalid escape sequence: \\" + next, currentLine, file);
                    }
                } else {
                    throw LoggerFacade.getLogger().panic("Escape character '\\' at end of string", currentLine, file);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
