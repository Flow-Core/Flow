package parser.analyzers.classes;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.classes.BaseInterfaceNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;

public class InterfaceAnalyzer implements TopAnalyzer {
    public AnalyzerResult parse(final Parser parser) {
        List<String> modifiers = parseModifiers(parser);

        parser.consume(TokenType.INTERFACE);

        final String name = parser.consume(TokenType.IDENTIFIER).value();

        final List<BaseInterfaceNode> implementedInterfaces = parseImplementedInterfaces(parser);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getInterfaceScope(), TokenType.CLOSE_BRACES);
        parser.consume(TokenType.CLOSE_BRACES);

        return new AnalyzerResult(
            new InterfaceNode(name, modifiers, implementedInterfaces, block),
            TerminationStatus.NO_TERMINATION
        );
    }

    public static List<BaseInterfaceNode> parseImplementedInterfaces(final Parser parser) {
        final List<BaseInterfaceNode> implementedInterfaces = new ArrayList<>();
        if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.consume(TokenType.COLON_OPERATOR);
            do {
                final Token interfaceName = parser.consume(TokenType.IDENTIFIER);
                implementedInterfaces.add(new BaseInterfaceNode(interfaceName.value()));
            } while (parser.check(TokenType.COMMA));
        }

        return implementedInterfaces;
    }
}