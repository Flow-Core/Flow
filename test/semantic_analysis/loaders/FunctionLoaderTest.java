package semantic_analysis.loaders;

import fakes.LoggerFake;
import generators.ast.classes.ClassNodeGenerator;
import generators.ast.components.BlockNodeGenerator;
import generators.ast.components.ParameterNodeGenerator;
import generators.ast.expressions.ExpressionBaseNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.ast.statements.ReturnStatementNodeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.FlowType;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.VoidLiteralNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.ArrayList;
import java.util.List;

class FunctionLoaderTest {
    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger();
    }

    @Test
    void test_valid_function_signature_should_add_to_scope() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(
                ParameterNodeGenerator.builder()
                    .type(new FlowType("Int", false, true))
                    .name("a").build()))
            .build();

        FunctionLoader.loadSignature(function, scope, false, false);
        Assertions.assertTrue(scope.symbols().findFunction("sum"), "Function should be added to scope");
    }

    @Test
    void test_function_with_unknown_return_type_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("UnknownType", false, false))
            .parameters(List.of(
                ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("a").build()))
            .build();
        FunctionLoader.loadSignature(function, scope, false, false);

        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Unknown return type should fail");
    }

    @Test
    void test_function_with_unknown_parameter_type_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(
                ParameterNodeGenerator.builder().type(new FlowType("UnknownType", false, false)).name("a").build()))
            .build();

        FunctionLoader.loadSignature(function, scope, false, false);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Unknown parameter type should fail");
    }

    @Test
    void test_function_with_valid_body_should_pass() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(
                ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("a").build()))
            .block(BlockNodeGenerator.builder()
                .children(List.of(
                    ReturnStatementNodeGenerator.builder()
                        .expression(ExpressionBaseNodeGenerator.builder().expression(new VariableReferenceNode("a")).build())
                        .build()))
                .build())
            .build();

        FunctionLoader.loadSignature(function, scope, false, false);
        FunctionLoader.loadBody(function, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(), "Valid function body should pass");
    }

    @Test
    void test_void_function_can_have_empty_return() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Void").build());

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("printHello")
            .returnType(new FlowType("Void", false, false))
            .parameters(new ArrayList<>())
            .block(BlockNodeGenerator.builder()
                .children(List.of(
                    ReturnStatementNodeGenerator.builder()
                        .expression(ExpressionBaseNodeGenerator.builder().expression(new VoidLiteralNode()).build())
                        .build()))
                .build()).build();

        FunctionLoader.loadSignature(function, scope, false, false);
        FunctionLoader.loadBody(function, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(), "Void function can have empty return");
    }

    @Test
    void test_void_function_can_have_no_return() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Void").build());

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("printHello")
            .returnType(new FlowType("Void", false, false))
            .parameters(new ArrayList<>())
            .block(BlockNodeGenerator.builder().children(new ArrayList<>()).build())
            .build();

        FunctionLoader.loadSignature(function, scope, false, false);
        FunctionLoader.loadBody(function, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(), "Void function should not require return");
    }

    @Test
    void test_function_with_empty_return_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(
                ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("a").build()))
            .block(BlockNodeGenerator.builder().children(new ArrayList<>()).build())
            .build();

        FunctionLoader.loadSignature(function, scope, false, false);
        FunctionLoader.loadBody(function, scope);

        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Function with missing return should fail");
    }

    @Test
    void test_overloaded_functions_should_pass() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        FunctionDeclarationNode function1 = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(
                ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("a").build()))
            .build();

        FunctionDeclarationNode function2 = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(
                ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("a").build(),
                ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("b").build()))
            .build();

        FunctionLoader.loadSignature(function1, scope, false, false);
        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors());

        FunctionLoader.loadSignature(function2, scope, false, false);
        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(), "Overloaded function should be allowed but failed.");
    }
}
