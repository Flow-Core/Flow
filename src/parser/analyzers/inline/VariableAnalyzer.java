package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.nodes.ExpressionNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.InitializedVariable;
import parser.nodes.variable.VariableReferenceNode;

public class VariableAnalyzer {
    public static VariableReferenceNode parseReference(final Parser parser) {
        final Token variable = parser.consume(TokenType.IDENTIFIER);

        return new VariableReferenceNode(variable.value());
    }

    public static VariableAssignmentNode parseAssignment(final Parser parser) {
        final Token variable = parser.consume(TokenType.IDENTIFIER);

        parser.consume(TokenType.EQUAL_OPERATOR);

        final ExpressionNode expr = ExpressionAnalyzer.parse(parser);

        return new VariableAssignmentNode(variable.value(), expr);
    }

    public static InitializedVariable parseInitialization(final Parser parser) {
        final Token modifier = parser.consume(TokenType.VAR, TokenType.VAL, TokenType.CONST);
        final Token name = parser.consume(TokenType.IDENTIFIER);

        // Check for type specification
        if (parser.peek().type() == TokenType.EQUAL_OPERATOR) {
            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = ExpressionAnalyzer.parse(parser);
            final VariableDeclarationNode declaration = new VariableDeclarationNode(modifier.value(), null, name.value());
            final VariableAssignmentNode assignment = new VariableAssignmentNode(name.value(), expr);

            return new InitializedVariable(declaration, assignment);
        } else if (parser.peek().type() == TokenType.COLON_OPERATOR) {
            parser.consume(TokenType.COLON_OPERATOR);
            final Token type = parser.consume(TokenType.IDENTIFIER);
            VariableDeclarationNode variableDeclarationNode = new VariableDeclarationNode(modifier.value(), type.value(), name.value());
            if (parser.peek().type() != TokenType.EQUAL_OPERATOR) {
                return new InitializedVariable(variableDeclarationNode, null);
            }

            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = ExpressionAnalyzer.parse(parser);
            final VariableAssignmentNode assignment = new VariableAssignmentNode(name.value(), expr);

            return new InitializedVariable(variableDeclarationNode, assignment);
        }

        return null;
    }
}
