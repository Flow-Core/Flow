package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;

import java.util.List;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseParametersList;

public class ConstructorAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        String accessModifier = "public";
        if (parser.check(TokenType.MODIFIER)) {
            accessModifier = TopAnalyzer.testFor(parser, TokenType.MODIFIER).value();
        }
        TopAnalyzer.testFor(parser, TokenType.CONSTRUCTOR);
        final int line = parser.peek().line();

        final List<ParameterNode> parameters = parseParametersList(parser);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(
            parser,
            AnalyzerDeclarations.getFunctionScope(),
            TokenType.CLOSE_BRACES
        );
        parser.consume(TokenType.CLOSE_BRACES);

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(
                new ConstructorNode(
                    accessModifier,
                    parameters,
                    new BodyNode(block)
                ),
                line,
                parser.file
            ),
            TerminationStatus.NO_TERMINATION
        );
    }
}
