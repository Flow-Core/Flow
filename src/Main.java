import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.ASTNode;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fullCodeExample = """
            package first.main
            
            import flow.util
            import flow.*
            import flow.networking.http as http
            
            func main() {
                val x = 10
                var ip: Ip = 127.0.0.1
                var d: Double = 124.5215
                d = 12.9++
                d += !10
                val i: Float = 12.0f + 24.2f / 2
                val z = 12f
                const x: Int = 10 * 2
                val y: String = "Hello, world!"
                const multiLine: String = "
                safasfasf
                sfsaf
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
            func test() {
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

        String smallCodeExample = "10 + 100 * 250 + (2 + 4)";

        final Lexer lexer = new Lexer(
            fullCodeExample
        );

        final List<Token> tokens = lexer.tokenize();
        System.out.println(tokens);

        final Parser parser = new Parser(tokens);

        ASTNode root = parser.parse();

        parser.printTree(root);
    }
}