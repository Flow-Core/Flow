package parser.analyzers.top;

import parser.Parser;
import parser.analyzers.inline.VariableAnalyzer;
import parser.nodes.ASTNode;

public class BlockAnalyzer {
    public static ASTNode parse(final Parser parser) {
        // TODO

        return VariableAnalyzer.parseDeclaration(parser);
    }
}
