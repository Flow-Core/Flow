package parser;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.ASTNode;

import java.util.List;

public class Parser {
    private final List<Token> tokens;

    private int currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        currentToken = 0;
    }

    public ASTNode parse() {
        return BlockAnalyzer.parse(this);
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

    public boolean check(TokenType type) {
        return peek().getType() == type;
    }

    public Token consume(TokenType type) throws RuntimeException {
        if (!check(type)) {
            throw new RuntimeException("'" + type + "' expected");
        }
        return advance();
    }

    public void printTree(ASTNode root) {
        System.out.println(root.toString());
    }
}