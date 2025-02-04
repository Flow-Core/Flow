package parser.analyzers.classes;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.AnalyzerDeclarations;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.BlockAnalyzer;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;

import java.util.List;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseParameters;

public class ConstructorAnalyzer extends TopAnalyzer {
    @Override
    public AnalyzerResult parse(final Parser parser) {
        String accessModifier = "public";
        if (parser.check(TokenType.MODIFIER)) {
            accessModifier = TopAnalyzer.testFor(parser, TokenType.MODIFIER).value();
        }
        TopAnalyzer.testFor(parser, TokenType.CONSTRUCTOR);

        final List<ParameterNode> parameters = parseParameters(parser);

        parser.consume(TokenType.OPEN_BRACES);
        final BlockNode block = BlockAnalyzer.parse(
            parser,
            AnalyzerDeclarations.getFunctionScope(),
            TokenType.CLOSE_BRACES
        );
        parser.consume(TokenType.CLOSE_BRACES);

        return new AnalyzerResult(
            new ConstructorNode(
                accessModifier,
                parameters,
                block
            ),
            TerminationStatus.NO_TERMINATION
        );
    }
}
