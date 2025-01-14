import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.ASTNode;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fullCodeExample = """
            /*
            124
            "starts here"
            this is a multiline comment
            here
            ends here */
           \s
            // this is a single line comment
            func main() {
                var ip: Ip = 127.0.0.1
                var d: Double = 124.5215
                val i: Float = 12.0f
                const x: Int = 10 * 2
                val y: String = "Hello, world!"
                const multiLine: String = "
                safasfasf
                sfsaf
                "
            }
            func test() {
            }
            
            interface A {
                func test()
            }
            
            class B: A {
                override func test() {
                    print("works!")
                }
            }
           \s""";

        String smallCodeExample = "class A { func test() {} }";

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