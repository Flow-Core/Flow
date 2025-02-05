import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.loaders.PackageMapper;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.SemanticAnalysis;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final String file1 = """
        func main(): A? {
            val x: A = new A()
            
            
        
            return null
        }
        
        func foo(a: A): A {
            return new A()
        }
        
        open class A {
            static func foo(a: A): A {
                return new A()
            }
        }
        
        class B : A() {}
        \s""";

        final String file2 = """
        """;

        final String file3 = """
        """;

        final BlockNode file1Root = getFileAST(file1);
        final BlockNode file2Root = getFileAST(file2);
        final BlockNode file3Root = getFileAST(file3);
        final Map<String, PackageWrapper> files = PackageMapper.map(List.of(file1Root, file2Root, file3Root));

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(files);
        semanticAnalysis.analyze();

        Parser.printTree(file1Root);
    }

    private static BlockNode getFileAST(final String file) {
        final Lexer lexer = new Lexer(file);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens);

        return parser.parse();
    }
}