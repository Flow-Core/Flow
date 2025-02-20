package semantic_analysis.visitors;

import fakes.LoggerFake;
import generators.ast.classes.ClassNodeGenerator;
import generators.ast.classes.FieldNodeGenerator;
import generators.ast.components.ParameterNodeGenerator;
import generators.ast.expressions.ExpressionBaseNodeGenerator;
import generators.ast.functions.FunctionCallNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.ast.variables.InitializedVariableNodeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.FlowType;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.IntegerLiteralNode;
import parser.nodes.literals.NullLiteral;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.ArrayList;
import java.util.List;

class ExpressionTraverseTest {
    private SymbolTable symbolTable;
    private Scope scope;

    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger();
    }

    @BeforeEach
    void setup() {
        symbolTable = SymbolTable.getEmptySymbolTable();
        scope = new Scope(null, symbolTable, null, Scope.Type.TOP);

        FunctionDeclarationNode plusFunction = FunctionNodeGenerator
            .builder()
            .name("plus")
            .returnType(new FlowType("Int", false, true)) // Use FlowType properly
            .parameters(
                List.of(
                    ParameterNodeGenerator.builder()
                        .type(new FlowType("Int", false, true))
                        .name("this")
                        .build(),
                    ParameterNodeGenerator.builder()
                        .type(new FlowType("Int", false, true))
                        .name("other")
                        .build()
                )
            ).build();

        ClassDeclarationNode intClass = new ClassNodeGenerator().name("Int").methods(List.of(plusFunction)).build();
        ClassDeclarationNode boolClass = new ClassNodeGenerator().name("Bool").build();

        symbolTable.classes().add(intClass);
        symbolTable.classes().add(boolClass);
    }

    @Test
    void test_integer_literal_should_return_int_type() {
        FlowType type = new ExpressionTraverse().traverse(
            ExpressionBaseNodeGenerator.builder().expression(new IntegerLiteralNode(10)).build(),
            scope
        );
        Assertions.assertEquals(new FlowType("Int", false, true).toString(), type.toString(), "Integer literal should be recognized as Int");
    }

    @Test
    void test_variable_reference_should_return_correct_type() {
        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    new VariableDeclarationNode("var", new FlowType("Int", false, true), "x")
                ).build()
            ).build();
        scope.symbols().fields().add(fieldNode);

        FlowType type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(new VariableReferenceNode("x")),
            scope
        );

        Assertions.assertEquals(new FlowType("Int", false, true).toString(), type.toString(), "Variable reference should return declared type");
    }

    @Test
    void test_null_literal_should_return_nullable_type() {
        FlowType type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(new NullLiteral()),
            scope
        );

        Assertions.assertEquals(new FlowType("null", true, false).toString(), type.toString(), "Null literal should return 'null' type");
    }

    @Test
    void test_binary_expression_should_return_correct_type() {
        ExpressionNode left = new IntegerLiteralNode(5);
        ExpressionNode right = new IntegerLiteralNode(3);

        ExpressionBaseNode binaryExpression = ExpressionBaseNodeGenerator.builder()
            .expression(new BinaryExpressionNode(left, right, "+"))
            .build();

        FlowType type = new ExpressionTraverse().traverse(binaryExpression, scope);

        Assertions.assertEquals(new FlowType("Int", false, true).toString(), type.toString(), "Binary expression should return correct inferred type");
    }

    @Test
    void test_function_call_should_return_correct_return_type() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("getNumber")
            .returnType(new FlowType("Int", false, true))
            .parameters(new ArrayList<>())
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("MyClass").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder().callerType("MyClass").name("getNumber").build();

        FlowType type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(functionCall),
            scope
        );

        Assertions.assertEquals(new FlowType("Int", false, true).toString(), type.toString(), "Function call should return correct return type");
    }

    @Test
    void test_static_method_call_should_return_correct_type() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("createInstance")
            .returnType(new FlowType("Person", false, false))
            .modifiers(List.of("static"))
            .parameters(new ArrayList<>())
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("Person").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder()
            .callerType("Person")
            .name("createInstance")
            .build();

        FlowType type = new ExpressionTraverse().traverse(
            ExpressionBaseNodeGenerator.builder().expression(functionCall).build(),
            scope
        );

        Assertions.assertEquals(new FlowType("Person", false, false).toString(), type.toString(), "Static method call should return correct type");
    }

    @Test
    void test_private_method_call_should_fail() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("internalMethod")
            .returnType(new FlowType("Int", false, true))
            .modifiers(List.of("private"))
            .parameters(new ArrayList<>())
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("Person").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder().callerType("Person").name("internalMethod").build();

        new ExpressionTraverse().traverse(new ExpressionBaseNode(functionCall), scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Calling a private method should throw an error");
    }
}
