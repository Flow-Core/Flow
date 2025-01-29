package semantic_analysis.visitors;

import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.expressions.UnaryOperatorNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.literals.NullLiteral;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.transformers.LiteralTransformer;

import java.util.List;

public class ExpressionTraverse {
    public String traverse(ExpressionBaseNode root, Scope scope) {
        ExpressionNode expression = root.expression;

        root.expression = transformValue(expression, scope);

        return determineType(expression, scope);
    }

    private static ExpressionNode transformValue(ExpressionNode expression, Scope scope) {
        if (expression instanceof LiteralNode literalNode) {
            return LiteralTransformer.transform(literalNode);
        }

        return transformOperators(expression, scope);
    }

    private static ExpressionNode transformOperators(ExpressionNode expression, Scope scope) {
        // TODO: Static members
        //       Change every member reference to work with base class and interfaces
        if (expression instanceof BinaryExpressionNode binaryExpression) {
            binaryExpression.left = transformValue(binaryExpression.left, scope);

            String leftType = determineType(binaryExpression.left, scope);
            ClassDeclarationNode leftTypeNode = scope.getClass(leftType);

            if (leftTypeNode == null)
                throw new SA_UnresolvedSymbolException(leftType);

            if (binaryExpression.operator.equals(".")) {
                if (binaryExpression.right instanceof VariableReferenceNode reference) {
                    FieldNode field = findField(
                        leftTypeNode.fields,
                        reference.variable
                    );

                    if (field == null) {
                        throw new SA_UnresolvedSymbolException(leftType + "." + reference.variable); // Log
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
                            argument -> determineType(argument.value, scope)
                        ).toList()
                    );

                    if (function == null) {
                        throw new SA_UnresolvedSymbolException(leftType + "." + call.name); // Log
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
                    throw new SA_SemanticError("Expected field or function"); // Log
                }
            }

            binaryExpression.right = transformValue(binaryExpression.right, scope);

            String rightType = determineType(binaryExpression.right, scope);

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


        } else if (expression instanceof UnaryOperatorNode unaryExpression) {
            unaryExpression.operand = transformValue(unaryExpression.operand, scope);

            String operandType = determineType(unaryExpression.operand, scope);
            ClassDeclarationNode operandTypeNode = scope.getClass(operandType);

            if (operandTypeNode == null)
                throw new SA_UnresolvedSymbolException(operandType);

            String operatorName = getOperatorName(unaryExpression.operator);

            FunctionDeclarationNode functionDecl = findMethodWithParameters(
                operandTypeNode.methods,
                operatorName,
                List.of()
            );

            if (functionDecl != null) {
                return new FunctionCallNode(
                    operandType,
                    operatorName,
                    List.of(
                        new ArgumentNode(null, unaryExpression.operand)
                    )
                );
            }
        }

        return null;
    }

    private static String determineType(ExpressionNode expression, Scope scope) {
        if (expression instanceof ObjectNode objectNode) {
            return objectNode.className;
        }
        if (expression instanceof VariableReferenceNode variable) {
            // TODO: Also check for nullability

            if (scope.findTypeDeclaration(variable.variable)) {
                return variable.variable; //TODO: Make it differentiate between static and instance members
            }

            FieldNode field = scope.getField(variable.variable);

            if (field != null) {
                return field.initialization.declaration.type;
            }

            throw new SA_UnresolvedSymbolException(variable.variable); // LOG
        }
        if (expression instanceof FunctionCallNode functionCall) {
            FunctionDeclarationNode function = findMethodWithParameters(
                scope,
                functionCall.name,
                functionCall.arguments.stream().map(
                    argument -> determineType(argument.value, scope)
                ).toList()
            );

            if (function != null) {
                return function.returnType;
            }

            throw new SA_UnresolvedSymbolException(functionCall.name); // LOG
        }
        if (expression instanceof NullLiteral) {
            return "null";
        }

        throw new SA_SemanticError("Could not resolve type"); // Log and return something
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

    private static FunctionDeclarationNode findMethodWithParameters(
        Scope scope,
        String name,
        List<String> parameterTypes
    ) {
        FunctionDeclarationNode declaration = null;

        while (declaration == null && scope != null && scope.parent() != null) {
            declaration = findMethodWithParameters(scope.symbols().functions(), name, parameterTypes);

            scope = scope.parent();
        }

        return declaration;
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
