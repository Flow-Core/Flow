package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.exceptions.PARSE_InvalidStatement;
import parser.exceptions.PARSE_TerminatorNotFound;
import parser.exceptions.PARSE_WrongAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class BlockAnalyzer {
    public static BlockNode parse(
        final Parser parser,
        final List<TopAnalyzer> analyzers,
        final TokenType... blockTerminators
    ) {
        if (blockTerminators.length == 0) {
            throw new IllegalArgumentException("Block terminator must not be empty");
        }

        final List<ASTNode> nodes = new ArrayList<>();

        TopAnalyzer.AnalyzerResult result = null;
        while (!parser.check(blockTerminators)) {
            if (parser.peek().isLineTerminator()) {
                parser.advance();
            }
            if (parser.check(blockTerminators)) {
                break;
            }

            for (final TopAnalyzer analyzer : analyzers) {
                parser.checkpoint();
                try {
                    result = analyzer.parse(parser);
                    if (result == null || result.node() == null) {
                        parser.rollback();
                        continue;
                    }
                } catch (PARSE_WrongAnalyzer exception) {
                    parser.rollback();
                    continue;
                }

                break;
            }
            System.out.println(parser.peek());
            System.out.println(result);
            if (result == null || result.node() == null) {
                throw new PARSE_InvalidStatement("Invalid statement");
            }
            if (result.terminationStatus() == TopAnalyzer.TerminationStatus.WAS_TERMINATED) {
                parser.advance();
            } else if (result.terminationStatus() == TopAnalyzer.TerminationStatus.NOT_TERMINATED && !parser.check(blockTerminators)) {
                throw new PARSE_TerminatorNotFound("Required newline or ';' after statement");
            }
            nodes.add(result.node());
        }

        return new BlockNode(nodes);
    }
}
