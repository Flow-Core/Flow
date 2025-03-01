package semantic_analysis.loaders;

import fakes.LoggerFake;
import generators.ast.classes.ClassNodeGenerator;
import generators.ast.classes.ConstructorNodeGenerator;
import generators.ast.classes.InterfaceNodeGenerator;
import generators.ast.components.ParameterNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.scopes.ScopeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.scopes.Scope;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassLoaderTest {
    private ClassLoader classLoader;
    private Scope testScope;

    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
        classLoader = new ClassLoader();
        testScope = ScopeGenerator
            .builder()
            .parent(
                ScopeGenerator.builder().build()
            ).build();
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger(); // Ensure a fresh logger state for each test
    }

    @Test
    void testHandlesBasicClass() {
        ClassDeclarationNode classNode = ClassNodeGenerator.builder().name("TestClass").build();
        classLoader.visit(classNode, testScope);
        assertFalse(LoggerFacade.getLogger().hasErrors(), "Basic class should not produce errors");
    }

    @Test
    void testHandlesInterfaceDeclaration() {
        InterfaceNode interfaceNode = InterfaceNodeGenerator.builder().name("TestInterface").build();
        classLoader.visit(interfaceNode, testScope);
        assertFalse(LoggerFacade.getLogger().hasErrors(), "Basic interface should not produce errors");
    }

    @Test
    void testAbstractClassMustImplementAbstractMethods() {
        FunctionDeclarationNode abstractMethod = FunctionNodeGenerator.builder()
            .name("abstractMethod")
            .returnType(new FlowType("Void", false, true))
            .modifiers(List.of("abstract"))
            .block(null)
            .build();

        ClassDeclarationNode abstractClass = ClassNodeGenerator.builder()
            .name("AbstractClass")
            .modifiers(List.of("abstract"))
            .methods(List.of(abstractMethod))
            .build();

        testScope.symbols().classes().add(abstractClass);
        classLoader.visit(abstractClass, testScope);

        assertFalse(LoggerFacade.getLogger().hasErrors(), "Abstract class should allow abstract methods");
    }

    @Test
    void testNonAbstractClassFailsToImplementAbstractMethods() {
        FunctionDeclarationNode abstractMethod = FunctionNodeGenerator.builder()
            .name("abstractMethod")
            .returnType(new FlowType("Void", false, true))
            .modifiers(List.of("abstract"))
            .build();

        ClassDeclarationNode abstractClass = ClassNodeGenerator.builder()
            .name("AbstractClass")
            .modifiers(List.of("abstract"))
            .methods(List.of(abstractMethod))
            .build();

        ClassDeclarationNode childClass = ClassNodeGenerator.builder()
            .name("ChildClass")
            .baseClasses(List.of(new BaseClassNode("AbstractClass", List.of())))
            .build();

        testScope.symbols().classes().add(abstractClass);
        testScope.symbols().classes().add(childClass);
        classLoader.visit(childClass, testScope);

        assertTrue(LoggerFacade.getLogger().hasErrors(), "Non-abstract class should fail to implement abstract methods");
    }

    @Test
    void testClassCannotExtendFinalClass() {
        ClassDeclarationNode finalClass = ClassNodeGenerator.builder()
            .name("FinalClass")
            .modifiers(List.of("final"))
            .build();

        ClassDeclarationNode childClass = ClassNodeGenerator.builder()
            .name("ChildClass")
            .baseClasses(List.of(new BaseClassNode("FinalClass", List.of())))
            .build();

        testScope.symbols().classes().add(finalClass);
        classLoader.visit(childClass, testScope);

        assertTrue(LoggerFacade.getLogger().hasErrors(), "Class should not be able to extend a final class");
    }

    @Test
    void testDetectsCircularInheritance() {
        ClassDeclarationNode classA = ClassNodeGenerator.builder()
            .name("A")
            .baseClasses(List.of(new BaseClassNode("B", List.of())))
            .build();

        ClassDeclarationNode classB = ClassNodeGenerator.builder()
            .name("B")
            .baseClasses(List.of(new BaseClassNode("A", List.of())))
            .build();

        testScope.symbols().classes().add(classA);
        testScope.symbols().classes().add(classB);

        assertThrows(RuntimeException.class, () -> classLoader.visit(classA, testScope), "Circular inheritance should be detected");
    }

    @Test
    void testClassCannotHaveDuplicateConstructors() {
        ConstructorNode constructor1 = ConstructorNodeGenerator.builder()
            .accessModifier("public")
            .parameters(List.of(
                ParameterNodeGenerator.builder()
                    .type(new FlowType("Int", false, false))
                    .name("x")
                    .build()))
            .build();

        ConstructorNode constructor2 = ConstructorNodeGenerator.builder()
            .accessModifier("public")
            .parameters(List.of(
                ParameterNodeGenerator.builder()
                    .type(new FlowType("Int", false, false))
                    .name("x")
                    .build()))
            .build();

        ClassDeclarationNode classNode = ClassNodeGenerator.builder()
            .name("DuplicateConstructorClass")
            .constructors(List.of(constructor1, constructor2))
            .build();

        classLoader.visit(classNode, testScope);
        assertTrue(LoggerFacade.getLogger().hasErrors(), "Class should not have duplicate constructors");
    }
}
