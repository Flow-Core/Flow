package semantic_analysis.visitors;

import fakes.LoggerFake;
import generators.ast.classes.ClassNodeGenerator;
import generators.ast.classes.FieldNodeGenerator;
import generators.ast.components.ArgumentNodeGenerator;
import generators.ast.components.ParameterNodeGenerator;
import generators.ast.expressions.ExpressionBaseNodeGenerator;
import generators.ast.functions.FunctionCallNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.ast.variables.FieldReferenceNodeGenerator;
import generators.ast.variables.InitializedVariableNodeGenerator;
import generators.ast.variables.VariableDeclarationNodeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.IntegerLiteralNode;
import parser.nodes.literals.NullLiteral;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.transformers.LiteralTransformer;

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
            .returnType("Int")
            .parameters(
                List.of(
                    ParameterNodeGenerator.builder()
                        .type("Int")
                        .name("this")
                        .build(),
                    ParameterNodeGenerator.builder()
                        .type("Int")
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
        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(
            ExpressionBaseNodeGenerator.builder().expression(LiteralTransformer.transform(new IntegerLiteralNode(10))).build(),
            scope
        );
        Assertions.assertEquals("Int", type.type(), "Integer literal should be recognized as Int");
    }

    @Test
    void test_variable_reference_should_return_correct_type() {
        FieldNode fieldNode = FieldNodeGenerator.builder()
            .initialization(
                InitializedVariableNodeGenerator.builder().declaration(
                    new VariableDeclarationNode("var", "Int", "x", false)
                ).build()
            ).build();
        scope.symbols().fields().add(fieldNode);

        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(new VariableReferenceNode("x")),
            scope
        );

        Assertions.assertEquals("Int", type.type(), "Variable reference should return declared type");
    }

    @Test
    void test_unknown_variable_should_throw_error() {
        new ExpressionTraverse().traverse(
            new ExpressionBaseNode(new VariableReferenceNode("y")),
            scope
        );
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Referencing an undeclared variable should throw an error");
    }

    @Test
    void test_binary_expression_should_return_correct_type() {
        ExpressionNode left = LiteralTransformer.transform(new IntegerLiteralNode(5));
        ExpressionNode right = LiteralTransformer.transform(new IntegerLiteralNode(3));

        ExpressionBaseNode binaryExpression = ExpressionBaseNodeGenerator.builder().expression(new BinaryExpressionNode(left, right, "+")).build();

        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(binaryExpression, scope);

        Assertions.assertEquals("Int", type.type(), "Binary expression should return correct inferred type");
    }

    @Test
    void test_invalid_binary_expression_should_throw_error() {
        ExpressionNode left = LiteralTransformer.transform(new IntegerLiteralNode(5));
        ExpressionNode right = new VariableReferenceNode("undefinedVar");

        ExpressionBaseNode binaryExpression = ExpressionBaseNodeGenerator.builder().expression(new BinaryExpressionNode(left, right, "+")).build();

        new ExpressionTraverse().traverse(binaryExpression, scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Using an undefined variable in a binary expression should throw an error"
        );
    }

    @Test
    void test_function_call_should_return_correct_return_type() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("getNumber")
            .returnType("Int")
            .parameters(new ArrayList<>())
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("MyClass").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder().callerType("MyClass").name("getNumber").build();

        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(functionCall),
            scope
        );

        Assertions.assertEquals("Int", type.type(), "Function call should return correct return type");
    }

    @Test
    void test_function_call_with_wrong_arguments_should_throw_error() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("doubleNumber")
            .returnType("Int")
            .parameters(List.of(
                ParameterNodeGenerator.builder().type("Int").name("x").build()
            ))
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("MyClass").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder().callerType("MyClass").name("doubleNumber").build();

        new ExpressionTraverse().traverse(new ExpressionBaseNode(functionCall), scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Calling a function without required arguments should throw an error");
    }

    @Test
    void test_null_literal_should_return_nullable_type() {
        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(new NullLiteral()),
            scope
        );

        Assertions.assertEquals("null", type.type(), "Null literal should return 'null' type");
        Assertions.assertTrue(type.isNullable(), "Null type should be nullable");
    }

    @Test
    void test_static_field_reference_should_return_correct_type() {
        FieldNode fieldNode = FieldNodeGenerator
            .builder()
            .modifiers(List.of("static"))
            .initialization(
                InitializedVariableNodeGenerator.builder()
                    .declaration(
                        VariableDeclarationNodeGenerator.builder().modifier("var").type("Int").name("x").isNullable(false).build()
                    ).build()
            ).build();

        ClassDeclarationNode classNode = ClassNodeGenerator.builder().name("Person").fields(List.of(fieldNode)).build();

        scope.symbols().classes().add(classNode);

        FieldReferenceNode fieldRef = FieldReferenceNodeGenerator.builder().holderType("Person").name("x").holder(new VariableReferenceNode("person")).build();

        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(fieldRef),
            scope
        );

        Assertions.assertEquals("Int", type.type(), "Field reference should return correct type");
    }

    @Test
    void test_field_reference_with_invalid_field_should_throw_error() {
        FieldReferenceNode fieldRef = FieldReferenceNodeGenerator.builder().holderType("Person").name("unknownField").holder(new VariableReferenceNode("person")).build();

        new ExpressionTraverse().traverse(new ExpressionBaseNode(fieldRef), scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Accessing an unknown field should throw an error");
    }

    @Test
    void test_private_field_access_should_fail() {
        FieldNode fieldNode = FieldNodeGenerator
            .builder()
            .modifiers(List.of("private"))
            .initialization(
                InitializedVariableNodeGenerator.builder()
                    .declaration(
                        new VariableDeclarationNode("var", "Int", "secret", false)
                    ).build()
            ).build();

        ClassDeclarationNode classNode = ClassNodeGenerator.builder().name("Person").fields(List.of(fieldNode)).build();
        scope.symbols().classes().add(classNode);

        FieldReferenceNode fieldRef = FieldReferenceNodeGenerator.builder().holderType("Person").name("secret").holder(new VariableReferenceNode("person")).build();

        new ExpressionTraverse().traverse(new ExpressionBaseNode(fieldRef), scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Accessing a private field should throw an error"
        );
    }

    @Test
    void test_protected_field_access_should_fail_if_not_subclass() {
        FieldNode fieldNode = FieldNodeGenerator
            .builder()
            .modifiers(List.of("protected"))
            .initialization(
                InitializedVariableNodeGenerator.builder()
                    .declaration(
                        new VariableDeclarationNode("var", "Int", "protectedField", false)
                    ).build()
            ).build();

        ClassDeclarationNode baseClass = ClassNodeGenerator.builder().name("Base").fields(List.of(fieldNode)).build();
        ClassDeclarationNode otherClass = ClassNodeGenerator.builder().name("OtherClass").build();

        scope.symbols().classes().add(baseClass);
        scope.symbols().classes().add(otherClass);

        FieldReferenceNode fieldRef = FieldReferenceNodeGenerator.builder().holderType("Base").name("protectedField").holder(new VariableReferenceNode("otherInstance")).build();

        new ExpressionTraverse().traverse(new ExpressionBaseNode(fieldRef), scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Accessing a protected field from a non-subclass should throw an error");
    }


    @Test
    void test_static_method_call_should_return_correct_type() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("createInstance")
            .returnType("Person")
            .modifiers(List.of("static"))
            .parameters(new ArrayList<>())
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("Person").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder()
            .callerType("Person")
            .name("createInstance")
            .build();

        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(
            ExpressionBaseNodeGenerator.builder().expression(functionCall).build(),
            scope
        );

        Assertions.assertEquals("Person", type.type(), "Static method call should return correct type");
    }

    @Test
    void test_private_method_call_should_fail() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("internalMethod")
            .returnType("Int")
            .modifiers(List.of("private"))
            .parameters(new ArrayList<>())
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("Person").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder().callerType("Person").name("internalMethod").build();

        new ExpressionTraverse().traverse(new ExpressionBaseNode(functionCall), scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Calling a private method should throw an error");
    }

    @Test
    void test_function_with_default_argument_should_resolve_correctly() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("greet")
            .returnType("String")
            .modifiers(List.of("static"))
            .parameters(List.of(
                ParameterNodeGenerator.builder().type("Int").name("age").defaultValue(new ExpressionBaseNode(new NullLiteral())).build()
            ))
            .build();

        symbolTable.classes().add(ClassNodeGenerator.builder().name("Person").methods(List.of(function)).build());

        FunctionCallNode functionCall = FunctionCallNodeGenerator
            .builder()
            .callerType("Person")
            .name("greet")
            .addArgument(ArgumentNodeGenerator.builder().value(ExpressionBaseNodeGenerator.builder().expression(LiteralTransformer.transform(new IntegerLiteralNode(10))).build()).build()
            ).build();

        ExpressionTraverse.TypeWrapper type = new ExpressionTraverse().traverse(
            new ExpressionBaseNode(functionCall),
            scope
        );

        Assertions.assertEquals("String", type.type(), "Function with default argument should resolve correctly");
    }

    @Test
    void test_private_method_in_subclass_should_fail() {
        FunctionDeclarationNode privateFunction = FunctionNodeGenerator.builder()
            .name("secretMethod")
            .returnType("Int")
            .modifiers(List.of("private"))
            .parameters(new ArrayList<>())
            .build();

        ClassDeclarationNode baseClass = ClassNodeGenerator.builder().name("BaseClass").methods(List.of(privateFunction)).build();
        ClassDeclarationNode subClass = ClassNodeGenerator.builder().name("SubClass").build();

        scope.symbols().classes().add(baseClass);
        scope.symbols().classes().add(subClass);

        FunctionCallNode functionCall = FunctionCallNodeGenerator.builder().callerType("BaseClass").name("secretMethod").build();

        new ExpressionTraverse().traverse(new ExpressionBaseNode(functionCall), scope);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Subclass should not be able to access private method from base class");
    }
}
