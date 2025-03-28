package parser.analyzers;

import parser.analyzers.classes.*;
import parser.analyzers.switches.CaseAnalyzer;
import parser.analyzers.switches.DefaultCaseAnalyzer;
import parser.analyzers.top.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AnalyzerDeclarations {
    private final static List<TopAnalyzer> FUNCTION_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> TOP_LEVEL_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> STATEMENT_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> CLASS_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> INTERFACE_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> SERVER_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> SWITCH_SCOPE = new ArrayList<>();
    private final static List<TopAnalyzer> INLINE_SCOPE = new ArrayList<>();

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

    public static List<TopAnalyzer> getServerScope() {
        return Collections.unmodifiableList(SERVER_SCOPE);
    }

    public static List<TopAnalyzer> getSwitchScope() {
        return Collections.unmodifiableList(SWITCH_SCOPE);
    }

    public static List<TopAnalyzer> getInlineScope() {
        return Collections.unmodifiableList(INLINE_SCOPE);
    }

    static {
        // Top Level Scope
        TOP_LEVEL_SCOPE.add(new FunctionDeclarationAnalyzer());
        TOP_LEVEL_SCOPE.add(new ClassAnalyzer());
        TOP_LEVEL_SCOPE.add(new InterfaceAnalyzer());
        TOP_LEVEL_SCOPE.add(new ServerAnalyzer());
        TOP_LEVEL_SCOPE.add(new FieldAnalyzer());
        TOP_LEVEL_SCOPE.add(new ImportAnalyzer());
        TOP_LEVEL_SCOPE.add(new PackageAnalyzer());

        // Function Scope
        FUNCTION_SCOPE.add(new VariableAssignmentAnalyzer(true));
        FUNCTION_SCOPE.add(new FunctionDeclarationAnalyzer());
        FUNCTION_SCOPE.add(new FieldAnalyzer());
        FUNCTION_SCOPE.add(new StatementAnalyzer());
        FUNCTION_SCOPE.add(new ExpressionAnalyzer());

        // Statement Scope
        STATEMENT_SCOPE.add(new VariableAssignmentAnalyzer(true));
        STATEMENT_SCOPE.add(new FunctionDeclarationAnalyzer());
        STATEMENT_SCOPE.add(new FieldAnalyzer());
        STATEMENT_SCOPE.add(new StatementAnalyzer());
        STATEMENT_SCOPE.add(new ExpressionAnalyzer());

        // Class Scope
        CLASS_SCOPE.add(new FunctionDeclarationAnalyzer());
        CLASS_SCOPE.add(new ClassAnalyzer());
        CLASS_SCOPE.add(new InterfaceAnalyzer());
        CLASS_SCOPE.add(new FieldAnalyzer());
        CLASS_SCOPE.add(new InitAnalyzer());
        CLASS_SCOPE.add(new ConstructorAnalyzer());

        // Interface Scope
        INTERFACE_SCOPE.add(new FunctionDeclarationAnalyzer());
        INTERFACE_SCOPE.add(new ClassAnalyzer());
        INTERFACE_SCOPE.add(new InterfaceAnalyzer());
        INTERFACE_SCOPE.add(new FieldAnalyzer());

        // Server Scope
        SERVER_SCOPE.add(new FunctionDeclarationAnalyzer());
        SERVER_SCOPE.add(new ClassAnalyzer());
        SERVER_SCOPE.add(new InterfaceAnalyzer());
        SERVER_SCOPE.add(new FieldAnalyzer());

        // Switch Scope
        SWITCH_SCOPE.add(new CaseAnalyzer());
        SWITCH_SCOPE.add(new DefaultCaseAnalyzer());

        // Inline Scope
        INLINE_SCOPE.add(new VariableAssignmentAnalyzer(true));
        INLINE_SCOPE.add(new ExpressionAnalyzer());
    }
}
