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

        return new VariableReferenceNode(variable.getValue());
    }

    public static VariableAssignmentNode parseAssignment(final Parser parser) {
        final Token variable = parser.consume(TokenType.IDENTIFIER);

        parser.consume(TokenType.EQUAL_OPERATOR);

        final ExpressionNode expr = ExpressionAnalyzer.parse(parser);

        return new VariableAssignmentNode(variable.getValue(), expr);
    }

    public static InitializedVariable parseInitialization(final Parser parser) {
        final Token modifier = parser.consume(TokenType.IDENTIFIER);
        final Token name = parser.consume(TokenType.IDENTIFIER);

        // Check for type specification
        if (parser.peek().getType() == TokenType.EQUAL_OPERATOR) {
            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = ExpressionAnalyzer.parse(parser);
            final VariableDeclarationNode declaration = new VariableDeclarationNode(modifier.getValue(), null, name.getValue());
            final VariableAssignmentNode assignment = new VariableAssignmentNode(name.getValue(), expr);

            return new InitializedVariable(declaration, assignment);
        } else if (parser.peek().getType() == TokenType.COLON_OPERATOR) {
            parser.consume(TokenType.COLON_OPERATOR);
            final Token type = parser.consume(TokenType.IDENTIFIER);
            if (parser.peek().getType() != TokenType.EQUAL_OPERATOR) {
                final VariableDeclarationNode declaration = new VariableDeclarationNode(modifier.getValue(), type.getValue(), name.getValue());
                return new InitializedVariable(declaration, null);
            }

            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = ExpressionAnalyzer.parse(parser);
            final VariableDeclarationNode declaration = new VariableDeclarationNode(modifier.getValue(), type.getValue(), name.getValue());
            final VariableAssignmentNode assignment = new VariableAssignmentNode(name.getValue(), expr);

            return new InitializedVariable(declaration, assignment);
        }

        return null;
    }
}
