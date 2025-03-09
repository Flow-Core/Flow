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
import parser.nodes.FlowType;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;
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

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(classNode)), null, "Test"), fileSymbolTable, packageWrapper);

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

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(classNode)), null, "Test"), fileSymbolTable, packageWrapper);

        Assertions.assertFalse(packageWrapper.scope().findClass("MyPrivateClass"), "Private class should not be in package scope");
        Assertions.assertTrue(fileSymbolTable.findClass("MyPrivateClass"), "Private class should be added to file scope");
    }

    @Test
    void test_duplicate_class_should_fail() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        ClassDeclarationNode class1 = ClassNodeGenerator.builder().name("MyClass").modifiers(List.of("public")).build();
        ClassDeclarationNode class2 = ClassNodeGenerator.builder().name("MyClass").modifiers(List.of("public")).build();

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(class1)), null, "Test"), fileSymbolTable, packageWrapper);
        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(class2)), null, "Test"), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Duplicate class should fail");
    }

    @Test
    void test_valid_interface_should_be_added_to_package_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        InterfaceNode interfaceNode = new InterfaceNode("MyInterface", List.of("public"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new BlockNode(new ArrayList<>()));

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(interfaceNode)), null, "Test"), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(packageWrapper.scope().findInterface("MyInterface"), "Interface should be added to package scope");
    }

    @Test
    void test_valid_function_should_be_added_to_package_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FunctionDeclarationNode functionNode = FunctionNodeGenerator.builder()
            .name("sum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("a").build()))
            .modifiers(List.of("public"))
            .build();

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(functionNode)), null, "Test"), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(packageWrapper.scope().findFunction("sum"), "Function should be added to package scope");
    }

    @Test
    void test_private_function_should_be_added_to_file_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FunctionDeclarationNode functionNode = FunctionNodeGenerator.builder()
            .name("privateSum")
            .returnType(new FlowType("Int", false, true))
            .parameters(List.of(ParameterNodeGenerator.builder().type(new FlowType("Int", false, true)).name("a").build()))
            .modifiers(List.of("private"))
            .build();

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(functionNode)), null, "Test"), fileSymbolTable, packageWrapper);

        Assertions.assertFalse(packageWrapper.scope().findFunction("privateSum"), "Private function should not be in package scope");
        Assertions.assertTrue(fileSymbolTable.findFunction("privateSum"), "Private function should be added to file scope");
    }

    @Test
    void test_valid_field_should_be_added_to_package_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .modifiers(List.of("public"))
            .initialization(InitializedVariableNodeGenerator.builder().declaration(VariableDeclarationNodeGenerator.builder().name("x").type(new FlowType("Int", false, true)).build()).build())
            .build();

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(fieldNode)), null, "Test"), fileSymbolTable, packageWrapper);

        Assertions.assertTrue(packageWrapper.scope().findField("x"), "Field should be added to package scope");
    }

    @Test
    void test_private_field_should_be_added_to_file_scope() {
        SymbolTable fileSymbolTable = SymbolTable.getEmptySymbolTable();
        PackageWrapper packageWrapper = new PackageWrapper("main", new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP));

        FieldNode fieldNode = FieldNodeGenerator.builder()
            .modifiers(List.of("private"))
            .initialization(InitializedVariableNodeGenerator.builder().declaration(VariableDeclarationNodeGenerator.builder().name("y").type(new FlowType("Int", false, true)).build()).build())
            .build();

        SignatureLoader.load(new FileWrapper(new BlockNode(List.of(fieldNode)), null, "Test"), fileSymbolTable, packageWrapper);

        Assertions.assertFalse(packageWrapper.scope().findField("y"), "Private field should not be in package scope");
        Assertions.assertTrue(fileSymbolTable.findField("y"), "Private field should be added to file scope");
    }
}
