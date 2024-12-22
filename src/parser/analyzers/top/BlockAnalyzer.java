package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class BlockAnalyzer {
    public static BlockNode parse(final Parser parser, final List<TopAnalyzer> analyzers) {
        final List<ASTNode> nodes = new ArrayList<>();

        ASTNode node = null;
        while (!parser.check(TokenType.CLOSE_BRACES)) {
            for (final TopAnalyzer analyzer : analyzers) {
                parser.checkpoint();
                try {
                    node = analyzer.parse(parser);
                } catch (RuntimeException exception) {
                    parser.rollback();
                }
            }
            if (node == null) {
                throw new RuntimeException("Invalid statement");
            }
            nodes.add(node);
        }

        return new BlockNode(nodes);
    }
}
