package semantic_analysis.visitors;

import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.Scope;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.transformers.LiteralTransformer;

import java.util.List;

public class ExpressionTraverse {
    public String traverse(ExpressionBaseNode root, Scope scope) {
        ExpressionNode expression = root.expression;

        root.expression = transformValue(expression, scope.symbols());

        return determineType(expression, scope.symbols());
    }

    private static ExpressionNode transformValue(ExpressionNode expression, SymbolTable symbolTable) {
        if (expression instanceof LiteralNode literalNode) {
            return LiteralTransformer.transform(literalNode);
        }

        return transformOperators(expression, symbolTable);
    }

    private static ExpressionNode transformOperators(ExpressionNode expression, SymbolTable symbolTable) {
        // TODO: Static members
        //       Change every member reference to work with base class and interfaces
        if (expression instanceof BinaryExpressionNode binaryExpression) {
            binaryExpression.left = transformValue(binaryExpression.left, symbolTable);

            String leftType = determineType(binaryExpression.left, symbolTable);
            ClassDeclarationNode leftTypeNode = symbolTable.getClass(leftType);

            if (binaryExpression.operator.equals(".")) {
                if (binaryExpression.right instanceof VariableReferenceNode reference) {
                    FieldNode field = findField(
                        leftTypeNode.fields,
                        reference.variable
                    );

                    if (field == null) {
                        throw new SA_UnresolvedSymbolException(leftType + "." + reference.variable);
                    }

                    return new FieldReferenceNode(
                        leftType,
                        reference.variable,
                        binaryExpression.left
                    );
                } else
                if (binaryExpression.right instanceof FunctionCallNode call) {
                    FunctionDeclarationNode function = findMethodWithParameters(
                        leftTypeNode.methods,
                        call.name,
                        call.arguments.stream().map(
                            argument -> determineType(argument.value, symbolTable)
                        ).toList()
                    );

                    if (function == null) {
                        throw new SA_UnresolvedSymbolException(leftType + "." + call.name);
                    }

                    call.arguments.add(
                        0,
                        new ArgumentNode(
                            null,
                            binaryExpression.left
                        )
                    );

                    return new FunctionCallNode(
                        leftType,
                        call.name,
                        call.arguments
                    );
                } else {
                    throw new SA_SemanticError("Expected field or function");
                }
            }

            binaryExpression.right = transformValue(binaryExpression.right, symbolTable);

            String rightType = determineType(binaryExpression.right, symbolTable);

            String operatorName = getOperatorName(binaryExpression.operator);

            FunctionDeclarationNode functionDecl = findMethodWithParameters(
                leftTypeNode.methods,
                operatorName,
                List.of(rightType)
            );

            if (functionDecl != null) {
                return new FunctionCallNode(
                    leftType,
                    operatorName,
                    List.of(
                        new ArgumentNode(null, binaryExpression.left),
                        new ArgumentNode(null, binaryExpression.right)
                    )
                );
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

        throw new SA_SemanticError("Could not resolve type");
    }

    private static FieldNode findField(
        List<FieldNode> fields,
        String name
    ) {
        return fields.stream().filter(
            interfaceNode -> interfaceNode.initialization.declaration.name.equals(name)
        ).findFirst().orElse(null);
    }

    private static FunctionDeclarationNode findMethodWithParameters(
        List<FunctionDeclarationNode> methods,
        String name,
        List<String> parameterTypes
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParameterTypes(method.parameters, parameterTypes))
            .findFirst().orElse(null);
    }

    private static boolean compareParameterTypes(
        List<ParameterNode> methods,
        List<String> parameterTypes
    ) {
        for (int i = 0; i < methods.size(); i++) {
            if (!methods.get(i).type.equals(parameterTypes.get(i)))
                return false;
        }

        return true;
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
