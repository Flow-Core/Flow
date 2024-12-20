package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.ASTNode;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclarationAnalyzer {
    public static ASTNode parse(Parser parser) {
        parser.consume(TokenType.FUNC);

        Token funcName = parser.consume(TokenType.IDENTIFIER);

        parser.consume(TokenType.OPEN_PARENTHESES);

        List<ArgumentNode> args = new ArrayList<>();

        while (true) {
            String type = parser.consume(TokenType.IDENTIFIER).getValue();
            String name = parser.consume(TokenType.IDENTIFIER).getValue();

            ArgumentNode arg = new ArgumentNode(type, name);

            args.add(arg);

            if (parser.check(TokenType.CLOSE_PARENTHESES)) break;

            parser.consume(TokenType.COMMA);
        }

        String returnType = "Void";

        if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.advance();
            returnType = parser.consume(TokenType.IDENTIFIER).getValue();
        }

        parser.consume(TokenType.OPEN_BRACES);

        BlockNode block = BlockAnalyzer.parse(parser);

        parser.consume(TokenType.CLOSE_BRACES);

        return new FunctionDeclarationNode(
                funcName.getValue(),
                returnType,
                args,
                block
        );
    }
}
