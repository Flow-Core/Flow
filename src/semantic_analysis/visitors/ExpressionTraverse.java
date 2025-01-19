package semantic_analysis.visitors;

import parser.nodes.*;
import parser.nodes.classes.ObjectNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.SymbolTable;

public class ExpressionTraverse {
    public static String traverse(ExpressionBaseNode root, SymbolTable symbolTable) {
        ExpressionNode expression = root.expression;

        transformExpr(expression);

        return determineType(expression, symbolTable);
    }

    private static ExpressionNode transformExpr(ExpressionNode expression) {
        if (expression instanceof LiteralNode literalNode) {
            // Call literal transformation
        }
        if (expression instanceof BinaryExpressionNode binaryExpr) {
            binaryExpr.left = transformExpr(binaryExpr.left);
            binaryExpr.right = transformExpr(binaryExpr.right);
        }
        if (expression instanceof UnaryOperatorNode unaryExpr) {
            unaryExpr.operand = transformExpr(unaryExpr.operand);
        }

        return expression;
    }

    private static String determineType(ExpressionNode expression, SymbolTable symbolTable) {
        // TODO: Everything below
        if (expression instanceof ObjectNode objectNode) {
            return objectNode.name;
        }
        if (expression instanceof VariableReferenceNode variable) {
            // Search variable in symbol table
            // Return variable type if found,
            // throw if not found

            // Also check for nullability
        }
        if (expression instanceof FunctionCallNode functionCall) {
            // Search function in symbol table
            // Return function type if found,
            // throw if not found
        }
        if (expression instanceof BinaryExpressionNode binaryExpr) {
            String leftType = determineType(binaryExpr.left, symbolTable);
            String rightType = determineType(binaryExpr.right, symbolTable);
        }
        if (expression instanceof UnaryOperatorNode unaryExpr) {
            unaryExpr.operand = transformExpr(unaryExpr.operand);
        }

        return null;
    }
}
