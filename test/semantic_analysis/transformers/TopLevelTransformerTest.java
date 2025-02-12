package semantic_analysis.transformers;

import generators.ast.classes.FieldNodeGenerator;
import generators.ast.components.BlockNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.ast.variables.InitializedVariableNodeGenerator;
import generators.ast.variables.VariableDeclarationNodeGenerator;
import generators.scopes.ScopeGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;

import java.util.ArrayList;
import java.util.List;

class TopLevelTransformerTest {

    @Test
    void test_transform_with_top_level_functions() {
        FunctionDeclarationNode function1 = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType("Int")
            .parameters(new ArrayList<>())
            .block(BlockNodeGenerator.builder().children(new ArrayList<>()).build())
            .build();

        FunctionDeclarationNode function2 = FunctionNodeGenerator.builder()
            .name("multiply")
            .returnType("Int")
            .parameters(new ArrayList<>())
            .block(BlockNodeGenerator.builder().children(new ArrayList<>()).build())
            .build();

        FileWrapper file = new FileWrapper(new BlockNode(new ArrayList<>(List.of(function1, function2))), ScopeGenerator.builder().build(), "Sum");
        TopLevelTransformer.transform(file, "");

        final ClassDeclarationNode generatedClass = file.scope().getClass("SumFl");
        Assertions.assertNotNull(generatedClass);
        Assertions.assertEquals(2, generatedClass.methods.size(), "Expected two top-level methods in transformed class");
        Assertions.assertTrue(generatedClass.methods.contains(function1), "Function 'sum' should be in the transformed class");
        Assertions.assertTrue(generatedClass.methods.contains(function2), "Function 'multiply' should be in the transformed class");
    }

    @Test
    void test_transform_with_top_level_fields() {
        FieldNode field1 = FieldNodeGenerator.builder()
            .modifiers(List.of("public"))
            .initialization(InitializedVariableNodeGenerator.builder()
                .declaration(VariableDeclarationNodeGenerator.builder().name("counter").type("Int").build())
                .build())
            .build();

        FieldNode field2 = FieldNodeGenerator.builder()
            .modifiers(List.of("public"))
            .initialization(InitializedVariableNodeGenerator.builder()
                .declaration(VariableDeclarationNodeGenerator.builder().name("message").type("String").build())
                .build())
            .build();

        FileWrapper file = new FileWrapper(new BlockNode(new ArrayList<>(List.of(field1, field2))), ScopeGenerator.builder().build(), "Counter");
        TopLevelTransformer.transform(file, "");

        final ClassDeclarationNode generatedClass = file.scope().getClass("CounterFl");
        Assertions.assertNotNull(generatedClass);
        Assertions.assertEquals(2, generatedClass.fields.size(), "Expected two top-level fields in transformed class");
        Assertions.assertTrue(generatedClass.fields.contains(field1), "Field 'counter' should be in the transformed class");
        Assertions.assertTrue(generatedClass.fields.contains(field2), "Field 'message' should be in the transformed class");
    }

    @Test
    void test_transform_with_mixed_top_level_elements() {
        FunctionDeclarationNode function = FunctionNodeGenerator.builder()
            .name("printHello")
            .returnType("Void")
            .parameters(new ArrayList<>())
            .block(BlockNodeGenerator.builder().children(new ArrayList<>()).build())
            .build();

        FieldNode field = FieldNodeGenerator.builder()
            .modifiers(List.of("public"))
            .initialization(InitializedVariableNodeGenerator.builder()
                .declaration(VariableDeclarationNodeGenerator.builder().name("count").type("Int").build())
                .build())
            .build();

        FileWrapper file = new FileWrapper(new BlockNode(new ArrayList<>(List.of(field, function))), ScopeGenerator.builder().build(), "Counter");
        TopLevelTransformer.transform(file, "");

        final ClassDeclarationNode generatedClass = file.scope().getClass("CounterFl");
        Assertions.assertNotNull(generatedClass);
        Assertions.assertEquals(1, generatedClass.methods.size(), "Expected one top-level method in transformed class");
        Assertions.assertEquals(1, generatedClass.fields.size(), "Expected one top-level field in transformed class");
    }

    @Test
    void test_transform_with_empty_file_should_return_empty_class() {
        FileWrapper file = new FileWrapper(new BlockNode(new ArrayList<>()), ScopeGenerator.builder().build(), "Empty");
        TopLevelTransformer.transform(file, "");

        final ClassDeclarationNode generatedClass = file.scope().getClass("Empty");
        Assertions.assertNull(generatedClass);
    }
}
