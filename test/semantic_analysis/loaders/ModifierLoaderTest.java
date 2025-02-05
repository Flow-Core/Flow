package semantic_analysis.loaders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import semantic_analysis.exceptions.SA_SemanticError;
import java.util.List;

class ModifierLoaderTest {

    @Test
    void test_valid_class_modifiers_should_pass() {
        Assertions.assertDoesNotThrow(() -> ModifierLoader.load(
            List.of("public", "open"),
            ModifierLoader.ModifierType.CLASS
        ));
    }

    @Test
    void test_duplicate_modifiers_should_fail() {
        Assertions.assertThrows(SA_SemanticError.class, () -> ModifierLoader.load(
            List.of("private", "private"),
            ModifierLoader.ModifierType.CLASS
        ), "Duplicate modifiers should throw an error");
    }

    @Test
    void test_conflicting_modifiers_should_fail() {
        Assertions.assertThrows(SA_SemanticError.class, () -> ModifierLoader.load(
            List.of("public", "private"),
            ModifierLoader.ModifierType.CLASS
        ), "Conflicting modifiers should throw an error");
    }

    @Test
    void test_invalid_modifier_should_fail() {
        Assertions.assertThrows(SA_SemanticError.class, () -> ModifierLoader.load(
            List.of("superfast"),
            ModifierLoader.ModifierType.CLASS
        ), "Invalid modifier should throw an error");
    }

    @Test
    void test_function_override_should_pass() {
        Assertions.assertDoesNotThrow(() -> ModifierLoader.load(
            List.of("public", "override"),
            ModifierLoader.ModifierType.FUNCTION
        ));
    }

    @Test
    void test_override_with_static_should_fail() {
        Assertions.assertThrows(SA_SemanticError.class, () -> ModifierLoader.load(
            List.of("override", "static"),
            ModifierLoader.ModifierType.FUNCTION
        ), "Override cannot be used with static");
    }

    @Test
    void test_valid_constructor_modifiers_should_pass() {
        Assertions.assertDoesNotThrow(() -> ModifierLoader.load(
            List.of("public"),
            ModifierLoader.ModifierType.CONSTRUCTOR
        ));
    }

    @Test
    void test_conflicting_constructor_modifiers_should_fail() {
        Assertions.assertThrows(SA_SemanticError.class, () -> ModifierLoader.load(
            List.of("public", "private"),
            ModifierLoader.ModifierType.CONSTRUCTOR
        ), "Conflicting constructor modifiers should throw an error");
    }
}
