package parser.analyzers.top;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.ExpressionAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.ExpressionNode;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclarationAnalyzer implements TopAnalyzer {
    @Override
    public ASTNode parse(Parser parser) {
        parser.consume(TokenType.FUNC);

        Token funcName = parser.consume(TokenType.IDENTIFIER);

        parser.consume(TokenType.OPEN_PARENTHESES);

        List<ParameterNode> args = new ArrayList<>();

        while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
            String name = parser.consume(TokenType.IDENTIFIER).value();
            parser.consume(TokenType.COLON_OPERATOR);
            String type = parser.consume(TokenType.IDENTIFIER).value();

            ExpressionNode defaultValue = null;
            if (parser.peek().type() == TokenType.EQUAL_OPERATOR) {
                parser.advance();
                defaultValue = ExpressionAnalyzer.parse(parser);
            }

            ParameterNode arg = new ParameterNode(type, name, defaultValue);

            args.add(arg);

            if (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                parser.consume(TokenType.COMMA);
            }
        }
        parser.advance();

        String returnType = "Void";

        if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.advance();
            returnType = parser.consume(TokenType.IDENTIFIER).value();
        }

        System.out.println(parser.peek());
        parser.consume(TokenType.OPEN_BRACES);

        BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getFunctionScope());

        parser.consume(TokenType.CLOSE_BRACES);

        return new FunctionDeclarationNode(
            funcName.value(),
            returnType,
            args,
            block
        );
    }
}
