package parser;

import lexer.token.Token;
import lexer.token.TokenType;
import logger.LoggerFacade;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Parser {
    private final List<Token> tokens;
    public final String file;

    private int currentToken;
    private final Stack<Integer> checkpoints;

    public Parser(final List<Token> tokens, final String file) {
        this.tokens = tokens;
        this.file = file;

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
                throw LoggerFacade.getLogger().panic("Expected " + Arrays.stream(expectedTypes).findFirst().get() + " but found '" + peek().type() + "'", peek().line(), file);
            }
            throw LoggerFacade.getLogger().panic("Expected one of " + Arrays.toString(expectedTypes) + " but found '" + peek().type() + "'", peek().line(), file);
        }

        return advance();
    }

    public static void printTree(final ASTNode root) {
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