package semantic_analysis.visitors;

import generators.ast.classes.ClassNodeGenerator;
import fakes.LoggerFake;
import generators.ast.components.BlockNodeGenerator;
import generators.ast.expressions.ExpressionBaseNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.ast.statements.*;
import generators.ast.variables.VariableAssignmentNodeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.FlowType;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.BooleanLiteralNode;
import parser.nodes.literals.IntegerLiteralNode;
import parser.nodes.statements.*;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.List;

class StatementTraverseTest {

    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger();
    }

    @Test
    void test_valid_if_statement_should_pass() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.FUNCTION);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        IfStatementNode ifStatement = IfStatementNodeGenerator.builder()
            .condition(ExpressionBaseNodeGenerator.builder()
                .expression(new BooleanLiteralNode(true))
                .build())
            .trueBranch(BlockNodeGenerator.builder().children(List.of()).build())
            .falseBranch(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        StatementTraverse.traverse(ifStatement, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(),
            "Valid if statement should pass");
    }

    @Test
    void test_invalid_if_statement_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.FUNCTION);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        IfStatementNode ifStatement = IfStatementNodeGenerator.builder()
            .condition(ExpressionBaseNodeGenerator.builder()
                .expression(new IntegerLiteralNode(10))
                .build())
            .trueBranch(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        StatementTraverse.traverse(ifStatement, scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "If statement should fail when condition is not a boolean");
    }

    @Test
    void test_valid_while_statement_should_pass() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.FUNCTION);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        WhileStatementNode whileStatement = WhileStatementNodeGenerator.builder()
            .condition(ExpressionBaseNodeGenerator.builder()
                .expression(new BooleanLiteralNode(true))
                .build())
            .loopBlock(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        StatementTraverse.traverse(whileStatement, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(),
            "Valid while loop should pass");
    }

    @Test
    void test_invalid_while_statement_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.FUNCTION);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        WhileStatementNode whileStatement = WhileStatementNodeGenerator.builder()
            .condition(ExpressionBaseNodeGenerator.builder()
                .expression(new IntegerLiteralNode(10))
                .build())
            .loopBlock(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        StatementTraverse.traverse(whileStatement, scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "While loop should fail when condition is not a boolean");
    }

    @Test
    void test_valid_for_loop_should_pass() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.FUNCTION);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        ForStatementNode forStatement = ForStatementNodeGenerator.builder()
            .initialization(VariableAssignmentNodeGenerator.builder()
                .variable(ExpressionBaseNodeGenerator.builder()
                    .expression(new VariableReferenceNode("i")).build())
                .value(ExpressionBaseNodeGenerator.builder()
                    .expression(new IntegerLiteralNode(0)).build())
                .operator("=")
                .build())
            .condition(ExpressionBaseNodeGenerator.builder()
                .expression(new BooleanLiteralNode(true))
                .build())
            .action(BlockNodeGenerator.builder().children(List.of()).build())
            .loopBlock(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        StatementTraverse.traverse(forStatement, scope);

        LoggerFacade.getLogger().dump();

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(),
            "Valid for loop should pass");
    }

    @Test
    void test_invalid_for_loop_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.FUNCTION);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        ForStatementNode forStatement = ForStatementNodeGenerator.builder()
            .initialization(VariableAssignmentNodeGenerator.builder()
                .variable(ExpressionBaseNodeGenerator.builder()
                    .expression(new VariableReferenceNode("i")).build())
                .value(ExpressionBaseNodeGenerator.builder()
                    .expression(new IntegerLiteralNode(0)).build())
                .operator("=")
                .build())
            .condition(ExpressionBaseNodeGenerator.builder()
                .expression(new IntegerLiteralNode(5))
                .build())
            .action(BlockNodeGenerator.builder().children(List.of()).build())
            .loopBlock(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        StatementTraverse.traverse(forStatement, scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "For loop should fail when condition is not a boolean");
    }

    @Test
    void test_switch_statement_should_pass() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.FUNCTION);
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        SwitchStatementNode switchStatement = SwitchStatementNodeGenerator.builder()
            .condition(ExpressionBaseNodeGenerator.builder()
                .expression(new IntegerLiteralNode(5))
                .build())
            .cases(List.of(
                CaseNodeGenerator.builder()
                    .value(ExpressionBaseNodeGenerator.builder().expression(new IntegerLiteralNode(5)).build())
                    .body(BlockNodeGenerator.builder().children(List.of()).build())
                    .build()
            ))
            .defaultBlock(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        StatementTraverse.traverse(switchStatement, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(),
            "Valid switch statement should pass");
    }

    @Test
    void test_return_statement_should_match_function_return_type() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of())
            .block(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        Scope scope = new Scope(null, symbolTable, function, Scope.Type.FUNCTION);

        ReturnStatementNode returnStatement = ReturnStatementNodeGenerator.builder()
            .expression(ExpressionBaseNodeGenerator.builder()
                .expression(new IntegerLiteralNode(10))
                .build())
            .build();

        StatementTraverse.traverse(returnStatement, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(),
            "Return type should match function return type");
    }

    @Test
    void test_return_statement_should_fail_for_mismatch() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Bool").build());
        symbolTable.classes().add(ClassNodeGenerator.builder().name("Int").build());

        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of())
            .block(BlockNodeGenerator.builder().children(List.of()).build())
            .build();

        Scope scope = new Scope(null, symbolTable, function, Scope.Type.FUNCTION);

        ReturnStatementNode returnStatement = ReturnStatementNodeGenerator.builder()
            .expression(ExpressionBaseNodeGenerator.builder()
                .expression(new BooleanLiteralNode(true))
                .build())
            .build();

        StatementTraverse.traverse(returnStatement, scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Return type mismatch should fail");
    }
}
