package parser.analyzers.top;

import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.VariableAnalyzer;
import parser.nodes.classes.FieldNode;

import java.util.List;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;

public class FieldAnalyzer implements TopAnalyzer {
    @Override
    public TopAnalyzer.AnalyzerResult parse(Parser parser) {
        final List<String> fieldModifiers = parseModifiers(parser);
        final FieldNode field = new FieldNode(fieldModifiers, VariableAnalyzer.parseInitialization(parser));

        return new AnalyzerResult(
            field,
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
