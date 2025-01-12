package parser.analyzers.classes;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.FunctionDeclarationAnalyzer;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.classes.InterfaceNode;

import java.util.ArrayList;
import java.util.List;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;

public class InterfaceAnalyzer implements TopAnalyzer {
    public InterfaceNode parse(final Parser parser) {
        parser.consume(TokenType.INTERFACE);

        List<String> modifiers = parseModifiers(parser);

        final String name = parser.consume(TokenType.IDENTIFIER).value();

        final List<String> implementedInterfaces = parseImplementedInterfaces(parser);

        parser.consume(TokenType.OPEN_BRACES);
        List<FunctionDeclarationNode> methods = new ArrayList<>();
        while (!parser.check(TokenType.CLOSE_BRACES)) {
            methods.add(FunctionDeclarationAnalyzer.parseFunctionSignature(parser));
        }
        parser.consume(TokenType.CLOSE_BRACES);

        return new InterfaceNode(name, modifiers, implementedInterfaces, methods);
    }

    private List<String> parseImplementedInterfaces(final Parser parser) {
        final List<String> implementedInterfaces = new ArrayList<>();
        if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.consume(TokenType.COLON_OPERATOR);
            do {
                final Token interfaceName = parser.consume(TokenType.IDENTIFIER);
                implementedInterfaces.add(interfaceName.value());
            } while (parser.check(TokenType.COMMA));
        }

        return implementedInterfaces;
    }
}