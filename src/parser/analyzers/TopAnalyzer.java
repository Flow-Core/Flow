package parser.analyzers;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.exceptions.PARSE_WrongAnalyzer;
import parser.nodes.ASTNode;

import java.util.Arrays;

public abstract class TopAnalyzer {
    public abstract AnalyzerResult parse(final Parser parser);

    public static Token testFor(final Parser parser, final TokenType... expectedTypes) throws PARSE_WrongAnalyzer {
        if (Arrays.stream(expectedTypes).noneMatch(tokenType -> parser.peek().type() == tokenType)) {
            throw new PARSE_WrongAnalyzer();
        }

        return parser.advance();
    }

    public record AnalyzerResult(
        ASTNode node,
        TerminationStatus terminationStatus
    ) {}

    public enum TerminationStatus {
        WAS_TERMINATED,
        NO_TERMINATION,
        NOT_TERMINATED
    }
}
