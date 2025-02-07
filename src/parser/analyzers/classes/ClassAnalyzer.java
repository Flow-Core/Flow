package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.nodes.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.analyzers.top.FieldAnalyzer;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.classes.*;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;
import static parser.analyzers.inline.IdentifierReferenceAnalyzer.parseArguments;

public class ClassAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        final List<String> modifiers = parseModifiers(parser);
        final int line = parser.peek().line();

        TopAnalyzer.testFor(parser, TokenType.CLASS);
        final String name = parser.consume(TokenType.IDENTIFIER).value();

        final List<FieldNode> classArgs = new ArrayList<>();
        if (parser.check(TokenType.OPEN_PARENTHESES)) {
            parser.advance();
            while (!parser.check(TokenType.CLOSE_PARENTHESES)) {
                classArgs.add((FieldNode) new FieldAnalyzer().parse(parser).node());
            }
            parser.advance();
        }

        final Supertypes supertypes = parseInheritance(parser, parser.peek().line());

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getClassScope(), TokenType.CLOSE_BRACES);
        parser.consume(TokenType.CLOSE_BRACES);

        final AnalyzerResult analyzerResult = new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(
                new ClassDeclarationNode(
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
                        .orElse(null),
                    null
                ),
                line,
                parser.file
            ),
            TerminationStatus.NO_TERMINATION
        );

        block.children.removeAll(((ClassDeclarationNode) analyzerResult.node()).fields);
        block.children.removeAll(((ClassDeclarationNode) analyzerResult.node()).methods);
        block.children.removeAll(((ClassDeclarationNode) analyzerResult.node()).constructors);
        block.children.remove(((ClassDeclarationNode) analyzerResult.node()).initBlock);
        ((ClassDeclarationNode) analyzerResult.node()).classBlock = block;

        return analyzerResult;
    }

    private Supertypes parseInheritance(final Parser parser, final int line) {
        final Supertypes supertypes = new Supertypes();

        if (parser.check(TokenType.COLON_OPERATOR)) {
            do {
                parser.advance();
                final String name = parser.consume(TokenType.IDENTIFIER).value();
                if (parser.check(TokenType.OPEN_PARENTHESES)) {
                    parser.advance();
                    supertypes.implementedClasses.add((BaseClassNode) ASTMetaDataStore.getInstance().addMetadata(new BaseClassNode(name, parseArguments(parser)), line, parser.file));
                    parser.consume(TokenType.CLOSE_PARENTHESES);
                } else {
                    supertypes.implementedInterfaces.add((BaseInterfaceNode) ASTMetaDataStore.getInstance().addMetadata(new BaseInterfaceNode(name), line, parser.file));
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
