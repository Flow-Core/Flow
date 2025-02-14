package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;
import parser.nodes.variable.VariableReferenceNode;

import static parser.analyzers.top.PackageAnalyzer.parseModulePath;

public class VariableAnalyzer {
    public static InitializedVariableNode parseInitialization(final Parser parser) {
        final Token modifier = TopAnalyzer.testFor(parser, TokenType.VAR, TokenType.VAL, TokenType.CONST);
        final Token name = parser.consume(TokenType.IDENTIFIER);

        // Check for type specification
        if (parser.check(TokenType.EQUAL_OPERATOR)) {
            parser.advance();
            final ExpressionNode expr = ExpressionAnalyzer.parseExpression(parser);
            final VariableDeclarationNode declaration = new VariableDeclarationNode(modifier.value(), null, name.value(), false);
            final VariableAssignmentNode assignment = new VariableAssignmentNode(
                new ExpressionBaseNode(new VariableReferenceNode(name.value())),
                "=",
                new ExpressionBaseNode(expr));

            return new InitializedVariableNode(declaration, assignment);
        } else if (parser.check(TokenType.COLON_OPERATOR)) {
            parser.advance();
            final String type = parseModulePath(parser);

            boolean isNullable = false;
            if (parser.check(TokenType.NULLABLE)) {
                isNullable = true;
                parser.advance();
            }

            VariableDeclarationNode variableDeclarationNode = new VariableDeclarationNode(
                modifier.value(),
                type,
                name.value(),
                isNullable
            );
            if (parser.peek().type() != TokenType.EQUAL_OPERATOR) {
                return new InitializedVariableNode(variableDeclarationNode, null);
            }

            parser.consume(TokenType.EQUAL_OPERATOR);
            final ExpressionNode expr = ExpressionAnalyzer.parseExpression(parser);
            final VariableAssignmentNode assignment = new VariableAssignmentNode(
                new ExpressionBaseNode(new VariableReferenceNode(name.value())),
                "=",
                new ExpressionBaseNode(expr));

            return new InitializedVariableNode(variableDeclarationNode, assignment);
        }

        return null;
    }
}
