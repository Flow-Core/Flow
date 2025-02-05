package semantic_analysis.transformers;

import generators.ast.components.ArgumentNodeGenerator;
import generators.ast.expressions.ExpressionBaseNodeGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.literals.IntegerLiteralNode;
import parser.nodes.literals.LiteralNode;

import java.util.List;

class LiteralTransformerTest {
    @Test
    void test_transform_to_object_call() {
        final LiteralNode literal = new IntegerLiteralNode(10);

        final ArgumentNode argument = ArgumentNodeGenerator.builder()
            .value(ExpressionBaseNodeGenerator.builder().expression(literal).build())
            .build();
        final ObjectNode expected = new ObjectNode(
            "Int",
            List.of(argument)
        );

        final ObjectNode actual = LiteralTransformer.transform(literal);

        Assertions.assertEquals(expected, actual, "Literal node should be transformed to object call node");
    }
}