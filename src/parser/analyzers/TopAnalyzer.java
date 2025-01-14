package parser.analyzers;

import parser.Parser;
import parser.nodes.ASTNode;

public interface TopAnalyzer {
    AnalyzerResult parse(final Parser parser);

    record AnalyzerResult(
        ASTNode node,
        TerminationStatus terminationStatus
    ) {}

    enum TerminationStatus {
        WAS_TERMINATED,
        NO_TERMINATION,
        NOT_TERMINATED
    }
}
