package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.analyzers.top.FieldAnalyzer;
import parser.analyzers.top.FunctionDeclarationAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.classes.*;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;
import static parser.analyzers.top.IdentifierReferenceAnalyzer.parseArguments;

public class ClassAnalyzer implements TopAnalyzer {
    final static List<TopAnalyzer> TOP_ANALYZERS = Arrays.asList(
        new FunctionDeclarationAnalyzer(),
        new ClassAnalyzer(),
        new FieldAnalyzer()
    );

    @Override
    public ASTNode parse(final Parser parser) {
        final List<String> modifiers = parseModifiers(parser);

        parser.consume(TokenType.CLASS);
        final String name = parser.consume(TokenType.IDENTIFIER).value();

        final List<FieldNode> classArgs = new ArrayList<>();
        if (parser.check(TokenType.OPEN_PARENTHESES)) {
            parser.advance();
            while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                classArgs.add(new FieldAnalyzer().parse(parser));
            }
            parser.advance();
        }

        final Supertypes supertypes = parseInheritance(parser);
        final BlockNode block = BlockAnalyzer.parse(parser, TOP_ANALYZERS);

        return new ClassDeclarationNode(
            name,
            modifiers,
            classArgs,
            supertypes.implementedClasses,
            supertypes.implementedInterfaces,
            block.children.stream()
                    .filter(child -> child instanceof FieldNode)
                    .map(child -> (FieldNode) child)
                    .collect(Collectors.toList()),
            block.children.stream()
                    .filter(child -> child instanceof FunctionDeclarationNode)
                    .map(child -> (FunctionDeclarationNode) child)
                    .collect(Collectors.toList()),
            block.children.stream()
                    .filter(child -> child instanceof ConstructorNode)
                    .map(child -> (ConstructorNode) child)
                    .collect(Collectors.toList()),
            block.children.stream()
                    .filter(child -> child instanceof BlockNode)
                    .map(child -> (BlockNode) child)
                    .findFirst()
                    .orElse(null)
        );
    }

    private Supertypes parseInheritance(final Parser parser) {
        final Supertypes supertypes = new Supertypes();

        if (parser.check(TokenType.COLON_OPERATOR)) {
            do {
                parser.advance();
                final String name = parser.consume(TokenType.IDENTIFIER).value();
                if (parser.check(TokenType.OPEN_PARENTHESES)) {
                    supertypes.implementedClasses.add(new BaseClassNode(name, parseArguments(parser)));
                } else {
                    supertypes.implementedInterfaces.add(new BaseInterfaceNode(name));
                }
            } while (parser.check(TokenType.COMMA));
        }

        return supertypes;
    }

    private static class Supertypes {
        public final List<BaseClassNode> implementedClasses = new ArrayList<>();
        public final List<BaseInterfaceNode> implementedInterfaces = new ArrayList<>();
    }
}
