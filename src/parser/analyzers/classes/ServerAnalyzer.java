package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.FlowTypeAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.FlowType;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.ServerNode;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.List;
import java.util.stream.Collectors;

import static parser.analyzers.classes.ClassAnalyzer.parseTypeParameters;

public class ServerAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(Parser parser) {
        TopAnalyzer.testFor(parser, TokenType.SERVER);

        final int line = parser.peek().line();

        final String name = parser.consume(TokenType.IDENTIFIER).value();
        final List<TypeParameterNode> typeParameters = parseTypeParameters(parser);

        parser.consume(TokenType.CONNECTION_OPERATOR);

        final FlowType protocol = FlowTypeAnalyzer.analyze(parser);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(parser, AnalyzerDeclarations.getServerScope(), TokenType.CLOSE_BRACES);
        parser.consume(TokenType.CLOSE_BRACES);

        final AnalyzerResult analyzerResult = new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(
                new ServerNode(
                    name,
                    protocol,
                    block.children.stream()
                        .filter(child -> child instanceof FieldNode)
                        .map(child -> (FieldNode) child)
                        .collect(Collectors.toList()),
                    block.children.stream()
                        .filter(child -> child instanceof FunctionDeclarationNode)
                        .map(child -> (FunctionDeclarationNode) child)
                        .collect(Collectors.toList()),
                    typeParameters
                ),
                line,
                parser.file
            ),
            TerminationStatus.NO_TERMINATION
        );

        block.children.removeAll(((ServerNode) analyzerResult.node()).fields);
        block.children.removeAll(((ServerNode) analyzerResult.node()).methods);
        ((ServerNode) analyzerResult.node()).classBlock = block;

        return analyzerResult;
    }
}
