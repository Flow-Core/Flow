package semantic_analysis.loaders;

import fakes.LoggerFake;
import generators.ast.classes.ClassNodeGenerator;
import generators.ast.components.BlockNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import generators.ast.packages.ImportNodeGenerator;
import generators.ast.packages.PackageNodeGenerator;
import logger.LoggerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.nodes.components.BlockNode;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ImportLoaderTest {
    private ImportLoader importLoader;
    private final Map<String, PackageWrapper> globalPackages = new HashMap<>();

    @BeforeEach
    void setupFlowStdLib() {
        importLoader = new ImportLoader();
        SymbolTable flowSymbols = SymbolTable.getEmptySymbolTable();

        flowSymbols.classes().add(ClassNodeGenerator.builder().name("String").build());
        flowSymbols.classes().add(ClassNodeGenerator.builder().name("Int").build());
        flowSymbols.classes().add(ClassNodeGenerator.builder().name("Bool").build());

        globalPackages.put("flow", new PackageWrapper("flow", List.of(), new Scope(null, flowSymbols, null, Scope.Type.TOP)));
    }

    @BeforeEach
    void setUp() {
        LoggerFacade.initLogger(new LoggerFake());
    }

    @AfterEach
    void tearDown() {
        LoggerFacade.clearLogger();
    }

    @Test
    void test_valid_import_class() {
        SymbolTable packageSymbols = SymbolTable.getEmptySymbolTable();
        packageSymbols.classes().add(ClassNodeGenerator.builder().name("MyClass").build());

        globalPackages.put("mypackage", new PackageWrapper("mypackage", List.of(), new Scope(null, packageSymbols, null, Scope.Type.TOP)));

        SymbolTable fileSymbols = SymbolTable.getEmptySymbolTable();
        BlockNode block = BlockNodeGenerator.builder()
            .children(List.of(
                ImportNodeGenerator.builder().module("mypackage.MyClass").alias("MyClass").build()
            ))
            .build();

        importLoader.load(block, fileSymbols, globalPackages);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors());
        Assertions.assertTrue(fileSymbols.findClass("MyClass"), "Class should be imported");
    }

    @Test
    void test_valid_import_function() {
        SymbolTable packageSymbols = SymbolTable.getEmptySymbolTable();
        packageSymbols.functions().add(FunctionNodeGenerator.builder().name("myFunction").returnType("Void").build());

        globalPackages.put("mypackage", new PackageWrapper("mypackage", List.of(), new Scope(null, packageSymbols, null, Scope.Type.TOP)));

        SymbolTable fileSymbols = SymbolTable.getEmptySymbolTable();
        BlockNode block = BlockNodeGenerator.builder()
            .children(List.of(
                ImportNodeGenerator.builder().module("mypackage.myFunction").alias("myFunction").build()
            ))
            .build();

        importLoader.load(block, fileSymbols, globalPackages);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors());
        Assertions.assertTrue(fileSymbols.findFunction("myFunction"), "Function should be imported");
    }

    @Test
    void test_valid_import_wildcard() {
        SymbolTable packageSymbols = SymbolTable.getEmptySymbolTable();
        packageSymbols.classes().add(ClassNodeGenerator.builder().name("MyClass").build());
        packageSymbols.functions().add(FunctionNodeGenerator.builder().name("myFunction").returnType("Void").build());

        globalPackages.put("mypackage", new PackageWrapper("mypackage", List.of(), new Scope(null, packageSymbols, null, Scope.Type.TOP)));

        SymbolTable fileSymbols = SymbolTable.getEmptySymbolTable();
        BlockNode block = BlockNodeGenerator.builder()
            .children(List.of(
                ImportNodeGenerator.builder().module("mypackage.*").alias("*").wildcard(true).build()
            ))
            .build();

        importLoader.load(block, fileSymbols, globalPackages);

        Assertions.assertFalse(LoggerFacade.getLogger().hasErrors());
        Assertions.assertTrue(fileSymbols.findClass("MyClass"), "Wildcard import should include classes");
        Assertions.assertTrue(fileSymbols.findFunction("myFunction"), "Wildcard import should include functions");
    }

    @Test
    void test_invalid_import_nonexistent_package() {
        SymbolTable fileSymbols = SymbolTable.getEmptySymbolTable();
        BlockNode block = BlockNodeGenerator.builder()
            .children(List.of(
                ImportNodeGenerator.builder().module("unknownpackage.MyClass").alias("MyClass").build()
            ))
            .build();

        importLoader.load(block, fileSymbols, globalPackages);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Importing from a non-existent package should fail");
    }

    @Test
    void test_invalid_import_nonexistent_symbol() {
        SymbolTable packageSymbols = SymbolTable.getEmptySymbolTable();

        globalPackages.put("mypackage", new PackageWrapper("mypackage", List.of(), new Scope(null, packageSymbols, null, Scope.Type.TOP)));

        SymbolTable fileSymbols = SymbolTable.getEmptySymbolTable();
        BlockNode block = BlockNodeGenerator.builder()
            .children(List.of(
                ImportNodeGenerator.builder().module("mypackage.UnknownClass").alias("UnknownClass").build()
            ))
            .build();

        importLoader.load(block, fileSymbols, globalPackages);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Importing a non-existent symbol should fail");
    }

    @Test
    void test_invalid_import_wildcard_renamed() {
        SymbolTable packageSymbols = SymbolTable.getEmptySymbolTable();
        globalPackages.put("mypackage", new PackageWrapper("mypackage", List.of(), new Scope(null, packageSymbols, null, Scope.Type.TOP)));

        SymbolTable fileSymbols = SymbolTable.getEmptySymbolTable();
        BlockNode block = BlockNodeGenerator.builder()
            .children(List.of(
                ImportNodeGenerator.builder().module("mypackage.*").alias("renamed").wildcard(true).build()
            ))
            .build();

        importLoader.load(block, fileSymbols, globalPackages);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Renaming a wildcard import should fail");
    }

    @Test
    void test_invalid_package_after_imports() {
        SymbolTable packageSymbols = SymbolTable.getEmptySymbolTable();
        globalPackages.put("mypackage", new PackageWrapper("mypackage", List.of(), new Scope(null, packageSymbols, null, Scope.Type.TOP)));

        SymbolTable fileSymbols = SymbolTable.getEmptySymbolTable();
        BlockNode block = BlockNodeGenerator.builder()
            .children(List.of(
                ImportNodeGenerator.builder().module("mypackage.MyClass").alias("MyClass").build(),
                PackageNodeGenerator.builder().packagePath("wrong.place").build()
            ))
            .build();

        importLoader.load(block, fileSymbols, globalPackages);
        Assertions.assertTrue(LoggerFacade.getLogger().hasErrors(), "Package declaration after imports should fail");
    }
}