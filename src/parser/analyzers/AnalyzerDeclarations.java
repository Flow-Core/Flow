package parser.analyzers;

import parser.analyzers.classes.ClassAnalyzer;
import parser.analyzers.classes.InterfaceAnalyzer;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.analyzers.top.FieldAnalyzer;
import parser.analyzers.top.FunctionDeclarationAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AnalyzerDeclarations {
    private final static List<TopAnalyzer> FUNCTION_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> TOP_LEVEL_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> STATEMENT_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> CLASS_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> INTERFACE_SCOPE = new ArrayList<>();

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

    public static List<TopAnalyzer> getClassScope() {
        return Collections.unmodifiableList(CLASS_SCOPE);
    }

    public static List<TopAnalyzer> getInterfaceScope() {
        return Collections.unmodifiableList(INTERFACE_SCOPE);
    }

    static {
        // Function Scope
        FUNCTION_SCOPE.add(new FunctionDeclarationAnalyzer());
        FUNCTION_SCOPE.add(new ExpressionAnalyzer());
        FUNCTION_SCOPE.add(new FieldAnalyzer());

        // Top Level Scope
        TOP_LEVEL_SCOPE.add(new FunctionDeclarationAnalyzer());
        TOP_LEVEL_SCOPE.add(new ClassAnalyzer());
        TOP_LEVEL_SCOPE.add(new InterfaceAnalyzer());
        TOP_LEVEL_SCOPE.add(new FieldAnalyzer());

        // Statement Scope
        STATEMENT_SCOPE.add(new FunctionDeclarationAnalyzer());
        STATEMENT_SCOPE.add(new ExpressionAnalyzer());
        STATEMENT_SCOPE.add(new FieldAnalyzer());

        // Class Scope
        CLASS_SCOPE.add(new FunctionDeclarationAnalyzer());
        CLASS_SCOPE.add(new ClassAnalyzer());
        CLASS_SCOPE.add(new InterfaceAnalyzer());
        CLASS_SCOPE.add(new FieldAnalyzer());

        // Interface Scope
        INTERFACE_SCOPE.add(new FunctionDeclarationAnalyzer());
        INTERFACE_SCOPE.add(new ClassAnalyzer());
        INTERFACE_SCOPE.add(new InterfaceAnalyzer());
        INTERFACE_SCOPE.add(new FieldAnalyzer());
    }
}
