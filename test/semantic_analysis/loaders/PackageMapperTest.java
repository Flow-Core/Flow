package semantic_analysis.loaders;

import generators.ast.components.BlockNodeGenerator;
import generators.ast.functions.FunctionNodeGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.packages.PackageNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.files.PackageWrapper;

import java.util.List;
import java.util.Map;

class PackageMapperTest {

    @Test
    void test_empty_input_should_return_empty_map() {
        Map<String, PackageWrapper> packages = PackageMapper.map(List.of(), List.of());

        Assertions.assertTrue(packages.isEmpty(), "Expected empty package map when no roots are provided");
    }

    @Test
    void test_single_file_with_no_package_should_go_to_empty_path() {
        FunctionDeclarationNode mainFunction = FunctionNodeGenerator.builder()
            .name("main")
            .returnType("Void")
            .parameters(List.of())
            .block(BlockNodeGenerator.builder().children(List.of()).build())
            .build();
        BlockNode block = new BlockNode(List.of(mainFunction));
        Map<String, PackageWrapper> packages = PackageMapper.map(List.of(block), List.of(""));

        Assertions.assertEquals(1, packages.size(), "Expected one package entry");
        Assertions.assertTrue(packages.containsKey(""), "Expected file to be under empty package path");
        Assertions.assertEquals(1, packages.get("").files().size(), "Expected one file in the empty package");
    }

    @Test
    void test_single_file_with_package_should_be_mapped_correctly() {
        BlockNode block = new BlockNode(List.of(new PackageNode("com.example")));
        Map<String, PackageWrapper> packages = PackageMapper.map(List.of(block), List.of(""));

        Assertions.assertEquals(1, packages.size(), "Expected one package entry");
        Assertions.assertTrue(packages.containsKey("com.example"), "Expected file to be in package 'com.example'");
        Assertions.assertEquals(1, packages.get("com.example").files().size(), "Expected one file in package");
    }

    @Test
    void test_multiple_files_same_package_should_group_together() {
        BlockNode block1 = new BlockNode(List.of(new PackageNode("com.example")));
        BlockNode block2 = new BlockNode(List.of(new PackageNode("com.example")));

        Map<String, PackageWrapper> packages = PackageMapper.map(List.of(block1, block2), List.of("", ""));

        Assertions.assertEquals(1, packages.size(), "Expected one package entry");
        Assertions.assertEquals(2, packages.get("com.example").files().size(), "Expected both files to be in the same package");
    }

    @Test
    void test_multiple_files_different_packages_should_be_separate() {
        BlockNode block1 = new BlockNode(List.of(new PackageNode("com.example")));
        BlockNode block2 = new BlockNode(List.of(new PackageNode("org.test")));

        Map<String, PackageWrapper> packages = PackageMapper.map(List.of(block1, block2), List.of("", ""));

        Assertions.assertEquals(2, packages.size(), "Expected two package entries");
        Assertions.assertTrue(packages.containsKey("com.example"), "Expected 'com.example' package");
        Assertions.assertTrue(packages.containsKey("org.test"), "Expected 'org.test' package");
    }

    @Test
    void test_each_file_gets_its_own_scope() {
        BlockNode block1 = new BlockNode(List.of(new PackageNode("com.example")));
        BlockNode block2 = new BlockNode(List.of(new PackageNode("com.example")));

        Map<String, PackageWrapper> packages = PackageMapper.map(List.of(block1, block2), List.of("", ""));

        FileWrapper file1 = packages.get("com.example").files().get(0);
        FileWrapper file2 = packages.get("com.example").files().get(1);

        Assertions.assertNotSame(file1.scope(), file2.scope(), "Each file should have its own scope");
    }
}
