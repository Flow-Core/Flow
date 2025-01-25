import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.FileMapper;
import semantic_analysis.FileWrapper;
import semantic_analysis.SemanticAnalysis;
import semantic_analysis.SymbolTable;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final String file1 = """
            package main.first
            \s
            import flow.util
            import flow.*
            import flow.networking.http as http
            \s
            func main() {
                for (x = 10, x < 15, x += 1)
                    print(x)
            \s
                val x = 10
                var ip: Ip = 127.0.0.1
                var d: Double = 124.5215
                d = 12.9++
                d += !10
                d = 12.9
                d += 10
                var x: String? = "test"
                x?.length
                x!!.length
                val i: Float = 12.0f + 24.2f / 2
                val z = 12f
                const x: Int = 10 * 2
                val y: String = "Hello, world!"
                const multiLine: String = "
                hello,
                world!
                "
                val d: Int = ip.toAddress().toString().length
                var NEW: Object = new Object();
                test(x = 2, 10, y = 8)
                test2(
                    x = 2,
                    10,
                    8
                )
                while (x > 5) {
                    print("hi!");
                }
                if (x == 5) {
                    print("test")
                } else if (x > 5) print("hi!")
                else
                    print("test2")
                foreach (x in collection) {
                }
            }
            func test(x: Int) {
                val y: C = C()
                var x = 10
                switch (x) {
                    case (10) {
                        print(x)
                    }
                    case (12) {
                        print(x + 2)
                    }
                    default {
                        print("error")
                    }
                }
            }
           \s
            interface A {
                func test()
            }
           \s
            class B: A {
                init {
                    print("B created!")
                }
                \s
                override func test() {
                    print("works!")
                }
            }
           \s""";

        final String file2 = """
        package main.second
        
        import main.first.B
        
        func test() {
            val b: B = new B();
        }
        """;

        final BlockNode file1Root = getFileAST(file1);
        final BlockNode file2Root = getFileAST(file2);
        final List<FileWrapper> files = FileMapper.map(List.of(file1Root, file2Root));

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(file1Root);
        final SymbolTable symbolTable = semanticAnalysis.analyze();
        System.out.println(symbolTable);
    }

    private static BlockNode getFileAST(final String file) {
        final Lexer lexer = new Lexer(file);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens);
        final BlockNode root = parser.parse();
        parser.printTree(root);

        return root;
    }
}