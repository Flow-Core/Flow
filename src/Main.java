import lexer.Lexer;
import lexer.token.Token;
import logger.LoggerFacade;
import logger.impl.ConsoleLogger;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.SemanticAnalysis;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.loaders.PackageMapper;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final String file1 = """
            package main.first
            \s
            func main() {
                for (x = 10, x < 15, x += 12 * 1)
                    print(x)
            \s
                val x = 10
                var ip: Ip = 127.0.0.1
                var d: Double = 124.5215
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
                val y: C = new C()
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
            open class B: A {
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
        
        import main.third.C
        
        func test() {
            val c: C = new C();
            c.test()
        }
        """;

        final String file3 = """
        package main.third
        
        import main.first.B

        class C: B() {
        
        }
        """;

        LoggerFacade.initLogger(new ConsoleLogger());

        final BlockNode file1Root = getFileAST(file1, "File1.fl");
        final BlockNode file2Root = getFileAST(file2, "File2.fl");
        final BlockNode file3Root = getFileAST(file3, "File3.fl");
        final Map<String, PackageWrapper> files = PackageMapper.map(List.of(file1Root, file2Root, file3Root));

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(files);
        semanticAnalysis.analyze();

        if (LoggerFacade.getLogger().hasErrors()) {
            LoggerFacade.getLogger().dump();
        }
    }

    private static BlockNode getFileAST(final String fileContent, final String fileName) {
        final Lexer lexer = new Lexer(fileContent, fileName);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens, fileName);
        final BlockNode root = parser.parse();
        Parser.printTree(root);

        return root;
    }
}