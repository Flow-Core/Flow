package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.inline.ExpressionAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class BlockAnalyzer {
    public static BlockNode parse(final Parser parser) {
        // TODO

        List<ASTNode> nodes = new ArrayList<>();

        ASTNode node;

        while (!parser.check(TokenType.CLOSE_BRACES)) {
            node = ExpressionAnalyzer.parse(parser);
            if (node == null) node = FunctionDeclarationAnalyzer.parse(parser);

            if (node != null)
                nodes.add(node);
        }

        return new BlockNode(nodes);
    }
}
