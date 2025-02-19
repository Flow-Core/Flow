package semantic_analysis.loaders;

import fakes.LoggerFake;
import generators.ast.classes.FieldNodeGenerator;
import generators.ast.expressions.ExpressionBaseNodeGenerator;
import generators.ast.variables.InitializedVariableNodeGenerator;
import generators.ast.variables.VariableAssignmentNodeGenerator;
import generators.ast.variables.VariableDeclarationNodeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BlockNode;
import parser.nodes.literals.IntegerLiteralNode;
import parser.nodes.literals.NullLiteral;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.ArrayList;
import java.util.List;

class VariableLoaderTest {

    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger();
    }

    @Test
    void test_variable_with_explicit_type_should_be_added_to_scope() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(new ClassDeclarationNode("Int", List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), new BlockNode(new ArrayList<>()), new BlockNode(new ArrayList<>())));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(VariableDeclarationNodeGenerator.builder().modifier("var").name("x").type("Int").build()).build()
            ).build();

        VariableLoader.loadDeclaration(fieldNode, scope);

        Assertions.assertTrue(scope.symbols().findField("x"), "Variable should be added to scope");
    }

    @Test
    void test_variable_with_inferred_type_should_be_added_to_scope() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(new ClassDeclarationNode("Int", List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), new BlockNode(new ArrayList<>()), new BlockNode(new ArrayList<>())));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    VariableDeclarationNodeGenerator.builder().modifier("var").name("x").type("Int").build()
                ).assignment(
                    VariableAssignmentNodeGenerator.builder()
                        .variable(ExpressionBaseNodeGenerator.builder()
                            .expression(new VariableReferenceNode("x")).build())
                        .value(ExpressionBaseNodeGenerator.builder()
                            .expression(new IntegerLiteralNode(10)).build())
                        .operator("=")
                        .build()
                ).build()
            ).build();

        VariableLoader.loadDeclaration(fieldNode, scope);

        Assertions.assertEquals("Int", fieldNode.initialization.declaration.type, "Variable type should be inferred as 'Int'");
        Assertions.assertTrue(scope.symbols().findField("x"), "Variable should be added to scope");
    }

    @Test
    void test_uninitialized_variable_with_no_explicit_type_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    VariableDeclarationNodeGenerator.builder()
                        .modifier("var")
                        .name("x")
                        .build()
                ).build()
            ).build();

        VariableLoader.loadDeclaration(fieldNode, scope);

        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Uninitialized variable should fail");
    }

    @Test
    void test_variable_with_unknown_type_should_fail() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    VariableDeclarationNodeGenerator.builder()
                        .modifier("var")
                        .name("x")
                        .modifier("var")
                        .type("UnknownType")
                        .build()
                ).build()).build();

        VariableLoader.loadDeclaration(fieldNode, scope);

        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Unknown type should fail");
    }

    @Test
    void test_variable_cannot_assign_null_to_non_nullable_type() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(new ClassDeclarationNode("Int", List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), new BlockNode(new ArrayList<>()), new BlockNode(new ArrayList<>())));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    VariableDeclarationNodeGenerator.builder()
                        .modifier("var")
                        .name("x")
                        .type("Int")
                        .build()
                ).assignment(
                    VariableAssignmentNodeGenerator.builder()
                        .variable(ExpressionBaseNodeGenerator.builder().expression(new VariableReferenceNode("x")).build())
                        .value(ExpressionBaseNodeGenerator.builder().expression(new NullLiteral()).build())
                        .operator("=")
                        .build()
                ).build()
            ).build();

        VariableLoader.loadDeclaration(fieldNode, scope);

        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Null assignment to non-nullable type should fail");
    }

    @Test
    void test_variable_can_assign_null_if_nullable() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(new ClassDeclarationNode("Int", List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), new BlockNode(new ArrayList<>()), new BlockNode(new ArrayList<>())));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    VariableDeclarationNodeGenerator.builder()
                        .modifier("var")
                        .name("x")
                        .type("Int")
                        .isNullable(true)
                        .build()
                ).assignment(
                    VariableAssignmentNodeGenerator.builder()
                        .variable(ExpressionBaseNodeGenerator.builder().expression(new VariableReferenceNode("x")).build())
                        .value(ExpressionBaseNodeGenerator.builder().expression(new NullLiteral()).build())
                        .operator("=")
                        .build()
                ).build()).build();

        VariableLoader.loadDeclaration(fieldNode, scope);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(), "Nullable variable should allow null");
    }

    @Test
    void test_cannot_reassign_final_variable() {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();
        Scope scope = new Scope(null, symbolTable, null, Scope.Type.TOP);
        symbolTable.classes().add(new ClassDeclarationNode("Int", List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), new BlockNode(new ArrayList<>()), new BlockNode(new ArrayList<>())));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    VariableDeclarationNodeGenerator.builder()
                        .modifier("val")
                        .name("x")
                        .type("Int")
                        .build()
                ).assignment(
                    VariableAssignmentNodeGenerator.builder()
                        .variable(ExpressionBaseNodeGenerator.builder().expression(new VariableReferenceNode("x")).build())
                        .value(ExpressionBaseNodeGenerator.builder().expression(new IntegerLiteralNode(10)).build())
                        .operator("=")
                        .build()
                ).build()).build();

        VariableLoader.loadDeclaration(fieldNode, scope);

        VariableAssignmentNode assignment = VariableAssignmentNodeGenerator.builder()
            .variable(ExpressionBaseNodeGenerator.builder().expression(new VariableReferenceNode("x")).build())
            .value(ExpressionBaseNodeGenerator.builder().expression(new IntegerLiteralNode(20)).build())
            .operator("=")
            .build();

        VariableLoader.loadAssignment(assignment, scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Cannot reassign final variable");
    }
}
