package parser;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.ASTNode;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Parser {
    private final List<Token> tokens;

    private int currentToken;
    private final Stack<Integer> checkpoints;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        checkpoints = new Stack<>();
        currentToken = 0;
    }

    public ASTNode parse() {
        return BlockAnalyzer.parse(this, AnalyzerDeclarations.getTopLevelScope());
    }

    public boolean isNotEOF() {
        return currentToken < tokens.size() - 1;
    }

    public Token advance() {
        if (isNotEOF()) {
            return tokens.get(currentToken++);
        }
        return tokens.get(currentToken);
    }

    public Token peek() {
        return tokens.get(currentToken);
    }

    public Token peek(int token) {
        return tokens.get(currentToken + token);
    }

    public boolean check(TokenType type) {
        return peek().type() == type;
    }

    public Token consume(TokenType type) throws RuntimeException {
        if (!check(type)) {
            throw new RuntimeException("'" + type + "' expected");
        }
        return advance();
    }

    public Token consume(TokenType... expectedTypes) throws RuntimeException {
        for (TokenType type : expectedTypes) {
            if (!check(type)) {
                throw new RuntimeException("Expected one of " + Arrays.toString(expectedTypes) + " but found '" + peek().value() + "'");
            }
        }

        return advance();
    }

    public void printTree(ASTNode root) {
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