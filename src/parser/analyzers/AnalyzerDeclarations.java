package parser.analyzers;

import parser.analyzers.top.FunctionDeclarationAnalyzer;
import parser.analyzers.top.IdentifierReferenceAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AnalyzerDeclarations {
    private final static List<TopAnalyzer> FUNCTION_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> TOP_LEVEL_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> STATEMENT_SCOPE = new ArrayList<>();

    private AnalyzerDeclarations() {}

    public static List<TopAnalyzer> getFunctionScope() {
        return Collections.unmodifiableList(FUNCTION_SCOPE);
    }

    public static List<TopAnalyzer> getTopLevelScope() {
        return Collections.unmodifiableList(TOP_LEVEL_SCOPE);
    }

    public static List<TopAnalyzer> getStatementScope() {
        return Collections.unmodifiableList(STATEMENT_SCOPE);
    }

    static {
        // Function Scope
        FUNCTION_SCOPE.add(new FunctionDeclarationAnalyzer());
        FUNCTION_SCOPE.add(new IdentifierReferenceAnalyzer());

        // Top Level Scope
        TOP_LEVEL_SCOPE.add(new FunctionDeclarationAnalyzer());

        // Statement Scope
        STATEMENT_SCOPE.add(new FunctionDeclarationAnalyzer());
        STATEMENT_SCOPE.add(new IdentifierReferenceAnalyzer());
    }
}
