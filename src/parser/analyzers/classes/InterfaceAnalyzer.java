package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.analyzers.inline.FlowTypeAnalyzer;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.FlowType;
import parser.nodes.classes.BaseInterfaceNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.ArrayList;
import java.util.List;

import static parser.analyzers.classes.ClassAnalyzer.parseTypeParameters;
import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;

public class InterfaceAnalyzer extends TopAnalyzer {
    public AnalyzerResult parse(final Parser parser) {
        List<String> modifiers = parseModifiers(parser);
        final int line = parser.peek().line();

        TopAnalyzer.testFor(parser, TokenType.INTERFACE);

        final String name = parser.consume(TokenType.IDENTIFIER).value();

        final List<TypeParameterNode> typeParameters = parseTypeParameters(parser);

        final List<BaseInterfaceNode> implementedInterfaces = parseImplementedInterfaces(parser);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getInterfaceScope(), TokenType.CLOSE_BRACES);
        parser.consume(TokenType.CLOSE_BRACES);

        final List<FunctionDeclarationNode> methods = block.children.stream()
            .filter(child -> child instanceof FunctionDeclarationNode)
            .map(child -> (FunctionDeclarationNode) child)
            .toList();
        block.children.removeAll(methods);

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(new InterfaceNode(name, modifiers, typeParameters, implementedInterfaces, methods, block), line, parser.file),
            TerminationStatus.NO_TERMINATION
        );
    }

    public static List<BaseInterfaceNode> parseImplementedInterfaces(final Parser parser) {
        final List<BaseInterfaceNode> implementedInterfaces = new ArrayList<>();
        if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.consume(TokenType.COLON_OPERATOR);
            do {
                final FlowType interfaceName = FlowTypeAnalyzer.analyze(parser);
                implementedInterfaces.add(new BaseInterfaceNode(interfaceName));
            } while (parser.check(TokenType.COMMA));
        }

        return implementedInterfaces;
    }
}