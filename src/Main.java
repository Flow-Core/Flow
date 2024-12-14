import lexer.Lexer;
import lexer.token.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final Lexer lexer = new Lexer(
        """
            /*
            124
            "starts here"
            this is a multiline comment
            here
            ends here */
           \s
            // this is a single line comment
            func main() {
                Ip ip = 127.0.0.1
                Double d = 124.5215
                Float i = 12.0f
                Int x = 10 * 2
                String y = "Hello, world!"
                String multiLine = "
                safasfasf
                sfsaf
                "
               \s
                while (x < 10) {
              \s
                  x = 100
                }
               \s
               \s
               \s
                if (x == 10) { x = 100 }
                else { x = 1000 }
               \s
                if (x >= 200) {
                    x = 100
                }
            }
           \s
            func test() {
            }
           \s"""
        );

        final List<Token> tokens = lexer.tokenize();
        System.out.println(tokens);
    }
}