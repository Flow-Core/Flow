package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.ExpressionNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.InitializedVariableNode;

public class VariableAnalyzer {
    public static VariableAssignmentNode parseAssignment(final Parser parser) {
        final Token variable = parser.consume(TokenType.IDENTIFIER);

        parser.consume(TokenType.EQUAL_OPERATOR);

        final ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();

        return new VariableAssignmentNode(variable.value(), expr);
    }

    public static InitializedVariableNode parseInitialization(final Parser parser) {
        final Token modifier = TopAnalyzer.testFor(parser, TokenType.VAR, TokenType.VAL, TokenType.CONST);
        final Token name = parser.consume(TokenType.IDENTIFIER);

        // Check for type specification
        if (parser.peek().type() == TokenType.EQUAL_OPERATOR) {
            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
            final VariableDeclarationNode declaration = new VariableDeclarationNode(modifier.value(), null, name.value());
            final VariableAssignmentNode assignment = new VariableAssignmentNode(name.value(), expr);

            return new InitializedVariableNode(declaration, assignment);
        } else if (parser.peek().type() == TokenType.COLON_OPERATOR) {
            parser.consume(TokenType.COLON_OPERATOR);
            final Token type = parser.consume(TokenType.IDENTIFIER);
            VariableDeclarationNode variableDeclarationNode = new VariableDeclarationNode(modifier.value(), type.value(), name.value());
            if (parser.peek().type() != TokenType.EQUAL_OPERATOR) {
                return new InitializedVariableNode(variableDeclarationNode, null);
            }

            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
            final VariableAssignmentNode assignment = new VariableAssignmentNode(name.value(), expr);

            return new InitializedVariableNode(variableDeclarationNode, assignment);
        }

        return null;
    }
}
