package parser.analyzers;

import parser.Parser;
import parser.nodes.ASTNode;

public interface TopAnalyzer {
    ASTNode parse(final Parser parser);
}
