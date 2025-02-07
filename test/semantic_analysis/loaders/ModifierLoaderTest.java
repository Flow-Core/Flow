package semantic_analysis.loaders;

import fakes.LoggerFake;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ModifierLoaderTest {

    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger();
    }

    @Test
    void test_valid_class_modifiers_should_pass() {
        Assertions.assertDoesNotThrow(() -> ModifierLoader.load(
            List.of("public", "open"),
            ModifierLoader.ModifierType.CLASS
        ));
    }

    @Test
    void test_duplicate_modifiers_should_fail() {
        ModifierLoader.load(List.of("private", "private"), ModifierLoader.ModifierType.CLASS);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Duplicate modifiers should throw an error");
    }

    @Test
    void test_conflicting_modifiers_should_fail() {
        ModifierLoader.load(List.of("public", "private"), ModifierLoader.ModifierType.CLASS);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Conflicting modifiers should throw an error");
    }

    @Test
    void test_invalid_modifier_should_fail() {
        ModifierLoader.load(List.of("superfast"), ModifierLoader.ModifierType.CLASS);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Invalid modifier should throw an error");
    }

    @Test
    void test_function_override_should_pass() {
        ModifierLoader.load(List.of("public", "override"), ModifierLoader.ModifierType.FUNCTION);
        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(), "Invalid modifier should throw an error");
    }

    @Test
    void test_override_with_static_should_fail() {
        ModifierLoader.load(List.of("override", "static"), ModifierLoader.ModifierType.FUNCTION);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Override cannot be used with static");
    }

    @Test
    void test_valid_constructor_modifiers_should_pass() {
        ModifierLoader.load(List.of("public"), ModifierLoader.ModifierType.CONSTRUCTOR);
        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors(), "Invalid modifier should throw an error");
    }

    @Test
    void test_conflicting_constructor_modifiers_should_fail() {
        ModifierLoader.load(List.of("public", "private"), ModifierLoader.ModifierType.CONSTRUCTOR);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Conflicting constructor modifiers should throw an error");
    }
}
