package parser.analyzers.inline;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.ASTMetaDataStore;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.analyzers.top.ExpressionAnalyzer;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;

import static parser.analyzers.top.PackageAnalyzer.parseModulePath;

public class VariableAnalyzer {
    public static InitializedVariableNode parseInitialization(final Parser parser) {
        final Token modifier = TopAnalyzer.testFor(parser, TokenType.VAR, TokenType.VAL, TokenType.CONST);
        final Token name = parser.consume(TokenType.IDENTIFIER);
        final int line = name.line();

        // Check for type specification
        if (parser.check(TokenType.EQUAL_OPERATOR)) {
            parser.advance();
            final ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
            final VariableDeclarationNode declaration = new VariableDeclarationNode(modifier.value(), null, name.value(), false);
            final VariableAssignmentNode assignment = new VariableAssignmentNode(
                name.value(),
                "=",
                new ExpressionBaseNode(expr)
            );

            return (InitializedVariableNode) ASTMetaDataStore.getInstance().addMetadata(new InitializedVariableNode(declaration, assignment), line, parser.file);
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
            final ExpressionNode expr = (ExpressionNode) new ExpressionAnalyzer().parse(parser).node();
            final VariableAssignmentNode assignment = new VariableAssignmentNode(
                name.value(),
                "=",
                new ExpressionBaseNode(expr));

            return (InitializedVariableNode) ASTMetaDataStore.getInstance().addMetadata(
                new InitializedVariableNode(
                    variableDeclarationNode,
                    assignment
                ),
                line,
                parser.file
            );
        }

        return null;
    }
}
