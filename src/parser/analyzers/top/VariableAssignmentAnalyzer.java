package parser.analyzers.top;

import lexer.token.TokenType;
import parser.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.exceptions.PARSE_WrongAnalyzer;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.VariableAssignmentNode;

public class VariableAssignmentAnalyzer extends TopAnalyzer {
    final boolean supportsAugmented;

    public VariableAssignmentAnalyzer(boolean supportsAugmented) {
        this.supportsAugmented = supportsAugmented;
    }

    @Override
    public AnalyzerResult parse(final Parser parser) {
        final AnalyzerResult variableResult = new ExpressionAnalyzer().parse(parser);
        if (variableResult == null) {
            throw new PARSE_WrongAnalyzer();
        }

        final ExpressionNode variable = (ExpressionNode) variableResult.node();
        final int line = parser.peek().line();
        final String operator = supportsAugmented ? TopAnalyzer.testFor(parser, TokenType.EQUAL_OPERATOR, TokenType.ASSIGNMENT_OPERATOR).value() : TopAnalyzer.testFor(parser, TokenType.EQUAL_OPERATOR).value();
        final ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

        return new AnalyzerResult(
            ASTMetaDataStore.getInstance().addMetadata(
                new VariableAssignmentNode(new ExpressionBaseNode(variable), operator, new ExpressionBaseNode(expr)),
                line,
                parser.file
            ),
            parser.check(TokenType.NEW_LINE, TokenType.SEMICOLON) ? TerminationStatus.WAS_TERMINATED : TerminationStatus.NOT_TERMINATED
        );
    }
}
