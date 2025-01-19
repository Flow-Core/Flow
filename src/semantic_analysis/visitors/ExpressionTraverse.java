package semantic_analysis.visitors;

import parser.nodes.*;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.transformers.LiteralTransformer;

import java.util.List;

public class ExpressionTraverse {
    public static String traverse(ExpressionBaseNode root, SymbolTable symbolTable) {
        ExpressionNode expression = root.expression;

        root.expression = transformValue(expression);

        return determineType(expression, symbolTable);
    } // x = 5 ExpressionBase(IntLiteral) -> ExpressionBase(ObjectNode(IntLiteral))

    private static ExpressionNode transformValue(ExpressionNode expression) {
        if (expression instanceof LiteralNode literalNode) {
            return LiteralTransformer.transform(literalNode);
        }

        return expression;
    }

    private static ExpressionNode transformOperators(ExpressionNode expression, SymbolTable symbolTable) {
        if (expression instanceof BinaryExpressionNode binaryExpression) {
            String leftType = determineType(binaryExpression.left, symbolTable);
            String rightType = determineType(binaryExpression.right, symbolTable);

            FunctionCallNode functionCall;

            for (ClassDeclarationNode classDecl : symbolTable.classes()) {
                if (classDecl.name.equals(leftType)) {
                    for (FunctionDeclarationNode functionDecl : classDecl.methods) {
                        if (functionDecl.parameters.get(0).type.equals(rightType)) {
                            functionCall = new FunctionCallNode(
                                leftType + binaryExpression.operator,
                                List.of(
                                    new ArgumentNode(null, binaryExpression.left),
                                    new ArgumentNode(null, binaryExpression.right)
                                )
                            );
                        }
                    }
                }
            }
        }

        return null;
    }

    private static String determineType(ExpressionNode expression, SymbolTable symbolTable) {
        // TODO: Everything below
        if (expression instanceof ObjectNode objectNode) {
            return objectNode.className;
        }
        if (expression instanceof VariableReferenceNode variable) {
            // Search variable in symbol table
            // Return variable type if found,
            // throw if not found

            // Also check for nullability
        } else if (expression instanceof FunctionCallNode functionCall) {
            // Search function in symbol table
            // Return function type if found,
            // throw if not found
        } else {

        }

        return null;
    }

    private static String getOperatorName(String operator) {
        return switch (operator) {
            case "+":
                yield "plus";
            case "-":
                yield "minus";
            case "*":
                yield "mul";
            case "/":
                yield "div";
            case "%":
                yield "mod";
            case "&&":
                yield "and";
            case "||":
                yield "or";
            case "!":
                yield "not";
            case "==":
                yield "equals";
            case "<=":
                yield "lessOrEquals";
            case ">=":
                yield "greaterOrEquals";
            case ">":
                yield "greaterThan";
            case "<":
                yield "lessThan";
            case "!=":
                yield "notEquals";
            case "++":
                yield "inc";
            case "--":
                yield "dec";
            default:
                yield null;
        };
    }
}
