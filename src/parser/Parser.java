package parser;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.top.BlockAnalyzer;
import parser.exceptions.PARSE_UnexpectedToken;
import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Parser {
    private final List<Token> tokens;

    private int currentToken;
    private final Stack<Integer> checkpoints;

    public Parser(final List<Token> tokens) {
        this.tokens = tokens;
        checkpoints = new Stack<>();
        currentToken = 0;
    }

    public BlockNode parse() {
        return BlockAnalyzer.parse(this, AnalyzerDeclarations.getTopLevelScope(), TokenType.EOF);
    }

    public boolean isNotEOF() {
        return currentToken < tokens.size() - 1;
    }

    public Token advance() {
        if (isNotEOF()) {
            peek();
            return tokens.get(currentToken++);
        }
        return peek();
    }

    public Token peek() {
        return tokens.get(currentToken);
    }

    public Token peek(final int token) {
        return tokens.get(currentToken + token);
    }

    public boolean check(final TokenType type) {
        return peek().type() == type;
    }

    public boolean check(final TokenType... type) {
        return Arrays.stream(type).anyMatch(tokenType -> peek().type() == tokenType);
    }

    public Token consume(final TokenType... expectedTypes) throws RuntimeException {
        if (expectedTypes.length == 0) {
            throw new IllegalArgumentException("Consume can't be empty");
        }

        if (check(TokenType.NEW_LINE)) {
            advance();
        }

        if (Arrays.stream(expectedTypes).noneMatch(tokenType -> peek().type() == tokenType)) {
            if (expectedTypes.length == 1) {
                throw new PARSE_UnexpectedToken("Expected " + Arrays.stream(expectedTypes).findFirst().get() + " but found '" + peek().value() + "'");
            }
            throw new PARSE_UnexpectedToken("Expected one of " + Arrays.toString(expectedTypes) + " but found '" + peek().value() + "'");
        }

        return advance();
    }

    public void printTree(final ASTNode root) {
        System.out.println(root.toString());
    }

    public void checkpoint() {
        checkpoints.push(currentToken);
    }

    public void rollback() {
        if (checkpoints.empty()) {
            return;
        }

        currentToken = checkpoints.pop();
    }
}