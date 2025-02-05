package semantic_analysis.loaders;

import fakes.LoggerFake;
import generators.ast.classes.ClassNodeGenerator;
import generators.ast.classes.FieldNodeGenerator;
import generators.ast.components.ParameterNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.ast.variables.InitializedVariableNodeGenerator;
import generators.ast.variables.VariableDeclarationNodeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.ArrayList;
import java.util.List;

class SignatureLoaderTest {

    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger();
    }

    @Test
    void test_valid_class_should_be_added_to_package_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        ClassDeclarationNode classNode = ClassNodeGenerator.builder()
            .name("MyClass")
            .modifiers(List.of("public"))
            .build();

        SignatureLoader.load(List.of(classNode), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(packageWrapper.scope().findClass("MyClass"), "Class should be added to package scope");
    }

    @Test
    void test_private_class_should_be_added_to_file_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        ClassDeclarationNode classNode = ClassNodeGenerator.builder()
            .name("MyPrivateClass")
            .modifiers(List.of("private"))
            .build();

        SignatureLoader.load(List.of(classNode), fileSymbolTable, packageWrapper);

        Assertions.assertFalse(packageWrapper.scope().findClass("MyPrivateClass"), "Private class should not be in package scope");
        Assertions.assertTrue(fileSymbolTable.findClass("MyPrivateClass"), "Private class should be added to file scope");
    }

    @Test
    void test_duplicate_class_should_fail() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        ClassDeclarationNode class1 = ClassNodeGenerator.builder().name("MyClass").modifiers(List.of("public")).build();
        ClassDeclarationNode class2 = ClassNodeGenerator.builder().name("MyClass").modifiers(List.of("public")).build();

        SignatureLoader.load(List.of(class1), fileSymbolTable, packageWrapper);
        SignatureLoader.load(List.of(class2), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Duplicate class should fail");
    }

    @Test
    void test_valid_interface_should_be_added_to_package_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        InterfaceNode interfaceNode = new InterfaceNode("MyInterface", List.of("public"), new ArrayList<>(), new ArrayList<>(), new BlockNode(new ArrayList<>()));

        SignatureLoader.load(List.of(interfaceNode), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(packageWrapper.scope().findInterface("MyInterface"), "Interface should be added to package scope");
    }

    @Test
    void test_valid_function_should_be_added_to_package_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FunctionDeclarationNode functionNode = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType("Int")
            .parameters(List.of(ParameterNodeGenerator.builder().type("Int").name("a").build()))
            .modifiers(List.of("public"))
            .build();

        SignatureLoader.load(List.of(functionNode), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(packageWrapper.scope().findFunction("sum"), "Function should be added to package scope");
    }

    @Test
    void test_private_function_should_be_added_to_file_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FunctionDeclarationNode functionNode = FunctionNodeGenerator.builder()
            .name("privateSum")
            .returnType("Int")
            .parameters(List.of(ParameterNodeGenerator.builder().type("Int").name("a").build()))
            .modifiers(List.of("private"))
            .build();

        SignatureLoader.load(List.of(functionNode), fileSymbolTable, packageWrapper);

        Assertions.assertFalse(packageWrapper.scope().findFunction("privateSum"), "Private function should not be in package scope");
        Assertions.assertTrue(fileSymbolTable.findFunction("privateSum"), "Private function should be added to file scope");
    }

    @Test
    void test_valid_field_should_be_added_to_package_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .modifiers(List.of("public"))
            .initialization(InitializedVariableNodeGenerator.builder().declaration(VariableDeclarationNodeGenerator.builder().name("x").type("Int").build()).build())
            .build();

        SignatureLoader.load(List.of(fieldNode), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(packageWrapper.scope().findField("x"), "Field should be added to package scope");
    }

    @Test
    void test_private_field_should_be_added_to_file_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .modifiers(List.of("private"))
            .initialization(InitializedVariableNodeGenerator.builder().declaration(VariableDeclarationNodeGenerator.builder().name("y").type("Int").build()).build())
            .build();

        SignatureLoader.load(List.of(fieldNode), fileSymbolTable, packageWrapper);

        Assertions.assertFalse(packageWrapper.scope().findField("y"), "Private field should not be in package scope");
        Assertions.assertTrue(fileSymbolTable.findField("y"), "Private field should be added to file scope");
    }
}
