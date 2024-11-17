import lexer.Lexer;
import lexer.token.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final Lexer lexer = new Lexer(
        """
            /*
            starts here
            this is a multiline comment
            here
            ends here */
            
            // this is a single line comment
            func main() {
                Int x = 10 * 2
                String y = "Hello, world!"
                String multiLine = "
                safasfasf
                sfsaf
                "
                
                while (x < 10) {
                  x = 100
                }
                
                
                
                if (x == 10) { x = 100 }
                else { x = 1000 }
                
                if (x >= 200) {
                    x = 100
                }
            }
            
            func test() {
            }
            """
        );

        final List<Token> tokens = lexer.tokenize();
        System.out.println(tokens);
    }
}