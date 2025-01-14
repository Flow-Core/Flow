package parser.analyzers.top;

import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.inline.VariableAnalyzer;
import parser.nodes.classes.FieldNode;

import java.util.List;

import static parser.analyzers.top.FunctionDeclarationAnalyzer.parseModifiers;

public class FieldAnalyzer implements TopAnalyzer {
    @Override
    public FieldNode parse(Parser parser) {
        final List<String> fieldModifiers = parseModifiers(parser);
        return new FieldNode(fieldModifiers, VariableAnalyzer.parseInitialization(parser));
    }
}
