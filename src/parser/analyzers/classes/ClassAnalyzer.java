package parser.analyzers.classes;

import lexer.token.TokenType;
import logger.LoggerFacade;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.FlowTypeAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.analyzers.top.FieldAnalyzer;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static parser.analyzers.inline.IdentifierReferenceAnalyzer.parseArguments;
import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;

public class ClassAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        final List<String> modifiers = parseModifiers(parser);
        final int line = parser.peek().line();

        TopAnalyzer.testFor(parser, TokenType.CLASS);
        final String name = parser.consume(TokenType.IDENTIFIER).value();

        final List<TypeParameterNode> typeParameters = parseTypeParameters(parser);

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
                    typeParameters,
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
                final FlowType className = FlowTypeAnalyzer.analyze(parser);
                if (parser.check(TokenType.OPEN_PARENTHESES)) {
                    parser.advance();
                    supertypes.implementedClasses.add((BaseClassNode) ASTMetaDataStore.getInstance().addMetadata(new BaseClassNode(className, parseArguments(parser)), line, parser.file));
                    parser.consume(TokenType.CLOSE_PARENTHESES);
                } else {
                    supertypes.implementedInterfaces.add((BaseInterfaceNode) ASTMetaDataStore.getInstance().addMetadata(new BaseInterfaceNode(className), line, parser.file));
                }
            } while (parser.check(TokenType.COMMA));
        }

        return supertypes;
    }

    public static List<TypeParameterNode> parseTypeParameters(final Parser parser) {
        List<TypeParameterNode> genericParameters = new ArrayList<>();

        if (parser.peek().value().equals("<")) {
            do {
                parser.advance();
                String typeName = parser.consume(TokenType.IDENTIFIER).value();
                FlowType bound = null;

                if (parser.check(TokenType.COLON_OPERATOR)) {
                    parser.advance();
                    bound = new FlowType(parser.consume(TokenType.IDENTIFIER).value(), false, false);
                }

                TypeParameterNode typeParameter;
                if (bound != null) {
                    typeParameter = new TypeParameterNode(typeName, bound);
                } else {
                    typeParameter = new TypeParameterNode(typeName);
                }
                genericParameters.add(typeParameter);
            } while (parser.check(TokenType.COMMA));

            if (!parser.peek().value().equals(">")) {
                throw LoggerFacade.getLogger().panic("Unexpected token", parser.peek().line(), parser.file);
            }
            parser.advance();
        }

        return genericParameters;
    }


    private static class Supertypes {
        public final List<BaseClassNode> implementedClasses = new ArrayList<>();
        public final List<BaseInterfaceNode> implementedInterfaces = new ArrayList<>();
    }
}
