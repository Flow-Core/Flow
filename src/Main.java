import compiler.code_generation.CodeGeneration;
import compiler.library_loader.LibLoader;
import compiler.packer.JarPacker;
import compiler.packer.Packer;
import compiler.packer.PackerFacade;
import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.SemanticAnalysis;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.loaders.PackageMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        final String file1 = """
        
        \s""";

        final String file2 = """
        import flow.*
        
        func main() {
            val x = new A()
            FlowIO.print(x)
        }
        
        func foo(a: Int, b: A) {}
        
        class A : B(2) {
            static val y = 10
        
            static func foo() {
                B.foo()
            }
        }
        
        open class B {
            constructor(a: Int) {}
        
            static func foo() {
                A.foo()
            }
        }
        """;

        final String file3 = """
        """;

        final BlockNode file1Root = getFileAST(file1);
        final BlockNode file2Root = getFileAST(file2);
        final BlockNode file3Root = getFileAST(file3);

        LibLoader.LibOutput libOutput = null;
        try {
            libOutput = LibLoader.loadLibraries("./libs");
        } catch (Exception e) {
            System.err.println("Could not load libraries");
            e.printStackTrace();
        }

        final Map<String, PackageWrapper> files = PackageMapper.map(
            List.of(file1Root, file2Root, file3Root),
            List.of("file1", "file2", "file3")
        );

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(
            files,
            libOutput != null ? libOutput.packages() : new HashMap<>()
        );
        final Map<String, PackageWrapper> packages = semanticAnalysis.analyze();

        Parser.printTree(file1Root);

        final CodeGeneration codeGeneration = new CodeGeneration(packages.get("").files().get(0));
        final List<CodeGeneration.ClassFile> bytes = codeGeneration.generate();

        File buildDir = new File("build");
        if (!buildDir.exists()) {
            if (!buildDir.mkdir()) {
                System.err.println("Couldn't make build dir");
            }
        }

        for (final CodeGeneration.ClassFile classFile : bytes) {
            File outputFile = new File(buildDir, classFile.name());
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(classFile.content());
            } catch (IOException e) {
                System.out.println("Failed to write class file");
            }
        }

        final Packer packer = new JarPacker();
        PackerFacade.initPacker(packer);

        try {
            PackerFacade.pack(
                    "output.jar",
                    "./build",
                    libOutput == null ? new ArrayList<>() : Arrays.asList(libOutput.libFiles()
                )
            );
        } catch (IOException e) {
            System.err.println("Couldn't pack jar file");
            e.printStackTrace();
        }
    }

    private static BlockNode getFileAST(final String file) {
        final Lexer lexer = new Lexer(file);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens);

        return parser.parse();
    }
}