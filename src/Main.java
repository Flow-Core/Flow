import compiler.code_generation.CodeGeneration;
import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.SemanticAnalysis;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.loaders.PackageMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final String file1 = """
        class Void {}
        class Int {
            func equals(current: Int, other: Int): Bool {
                return true
            }
        }
        class Double {}
        class Bool {}
        
        func main() {
            val x = 10
            
            if (x == 10) {
            }
        }
        \s""";

        final String file2 = """
        """;

        final String file3 = """
        """;

        final BlockNode file1Root = getFileAST(file1);
        final BlockNode file2Root = getFileAST(file2);
        final BlockNode file3Root = getFileAST(file3);
        final Map<String, PackageWrapper> files = PackageMapper.map(
            List.of(file1Root, file2Root, file3Root),
            List.of("file1", "file2", "file3")
        );

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(files);
        final Map<String, PackageWrapper> packages = semanticAnalysis.analyze();

        Parser.printTree(file1Root);

        final CodeGeneration codeGeneration = new CodeGeneration(packages.get("").files().get(0));
        final List<CodeGeneration.ClassFile> bytes = codeGeneration.generate();

        for (final CodeGeneration.ClassFile classFile : bytes) {
            try (FileOutputStream fos = new FileOutputStream(classFile.name())) {
                fos.write(classFile.content());
            } catch (IOException e) {
                System.out.println("Failed to write class file");
            }
        }
    }

    private static BlockNode getFileAST(final String file) {
        final Lexer lexer = new Lexer(file);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens);

        return parser.parse();
    }
}