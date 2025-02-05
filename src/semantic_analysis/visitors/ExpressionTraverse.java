package semantic_analysis.visitors;

import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
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
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.scopes.Scope;
import semantic_analysis.transformers.LiteralTransformer;

import java.util.List;

import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;

public class ExpressionTraverse {
    public TypeWrapper traverse(ExpressionBaseNode root, Scope scope) {
        root.expression = transformValue(root.expression, scope);

        TypeWrapper type = determineType(root.expression, scope);

        if (type.isTypeReference)
            throw new SA_SemanticError("Expression expected"); // Log

        return type;
    }

    private static ExpressionNode transformValue(ExpressionNode expression, Scope scope) {
        if (expression instanceof LiteralNode literalNode) {
            return LiteralTransformer.transform(literalNode);
        }

        return transformOperators(expression, scope);
    }

    private static ExpressionNode transformOperators(ExpressionNode expression, Scope scope) {
        if (expression instanceof BinaryExpressionNode binaryExpression) {
            binaryExpression.left = transformValue(binaryExpression.left, scope);

            TypeWrapper leftType = determineType(binaryExpression.left, scope);

            ClassDeclarationNode leftTypeNode = scope.getClass(leftType.type);

            if (leftTypeNode == null)
                throw new SA_UnresolvedSymbolException(leftType.type); // Log

            if (binaryExpression.operator.equals(".")) {
                if (binaryExpression.right instanceof VariableReferenceNode reference) {
                    if (scope.findTypeDeclaration(reference.variable)) {
                        throw new SA_SemanticError("Cannot access nested types"); // Log
                    }

                    FieldNode field = leftTypeNode.findField(
                        scope,
                        reference.variable
                    );

                    if (field == null) {
                        throw new SA_UnresolvedSymbolException(leftType.type + "." + reference.variable); // Log
                    }

                    return new FieldReferenceNode(
                        leftType.type,
                        reference.variable,
                        binaryExpression.left
                    );
                } else
                if (binaryExpression.right instanceof FunctionCallNode call) {
                    List<FunctionDeclarationNode> functions = leftTypeNode.findMethodsWithName(
                        scope,
                        call.name
                    );

                    FunctionDeclarationNode function = findMethodWithParameters(
                        scope,
                        functions,
                        call.name,
                        call.arguments.stream().map(
                            argument -> new ExpressionTraverse().traverse(argument.value, scope)
                        ).toList()
                    );

                    if (function == null) {
                        if (functions.isEmpty())
                            throw new SA_UnresolvedSymbolException(leftType.type + "." + call.name); // Log
                        else
                            throw new SA_SemanticError("None of the overrides for '" +
                                leftType.type + "." + call.name +
                                "' match the argument list");
                    }

                    if (!leftType.isTypeReference)
                        call.arguments.add(
                            0,
                            new ArgumentNode(
                                null,
                                new ExpressionBaseNode(
                                    binaryExpression.left
                                )
                            )
                        );

                    return new FunctionCallNode(
                        leftType.type,
                        call.name,
                        call.arguments
                    );
                } else {
                    throw new SA_SemanticError("Expected field or function"); // Log
                }
            }

            if (leftType.isTypeReference)
                throw new SA_SemanticError("Expression expected");

            binaryExpression.right = transformValue(binaryExpression.right, scope);

            TypeWrapper rightType = determineType(binaryExpression.right, scope);

            if (rightType.isTypeReference)
                throw new SA_SemanticError("Expression expected");

            String operatorName = getOperatorName(binaryExpression.operator);

            FunctionDeclarationNode functionDecl = findMethodWithParameters(
                scope,
                leftTypeNode.methods,
                operatorName,
                List.of(rightType)
            );

            if (functionDecl != null) {
                return new FunctionCallNode(
                    leftType.type,
                    operatorName,
                    List.of(
                        new ArgumentNode(null, new ExpressionBaseNode(binaryExpression.left)),
                        new ArgumentNode(null, new ExpressionBaseNode(binaryExpression.right))
                    )
                );
            }


        } else if (expression instanceof UnaryOperatorNode unaryExpression) {
            unaryExpression.operand = transformValue(unaryExpression.operand, scope);

            TypeWrapper operandType = determineType(unaryExpression.operand, scope);

            if (operandType.isTypeReference)
                throw new SA_SemanticError("Expression expected");

            ClassDeclarationNode operandTypeNode = scope.getClass(operandType.type);

            if (operandTypeNode == null)
                throw new SA_UnresolvedSymbolException(operandType.type);

            String operatorName = getOperatorName(unaryExpression.operator);

            FunctionDeclarationNode functionDecl = findMethodWithParameters(
                scope,
                operandTypeNode.methods,
                operatorName,
                List.of()
            );

            if (functionDecl != null) {
                return new FunctionCallNode(
                    operandType.type,
                    operatorName,
                    List.of(
                        new ArgumentNode(null, new ExpressionBaseNode(unaryExpression.operand))
                    )
                );
            }
        }

        return expression;
    }

    private static TypeWrapper determineType(ExpressionNode expression, Scope scope) {
        if (expression instanceof ObjectNode objectNode) {
            return new TypeWrapper(objectNode.className, false, false);
        }
        if (expression instanceof VariableReferenceNode variable) {
            if (scope.findTypeDeclaration(variable.variable)) {
                return new TypeWrapper(variable.variable, true, false);
            }

            FieldNode field = scope.getField(variable.variable);

            if (field != null) {
                return new TypeWrapper(
                    field.initialization.declaration.type,
                    false,
                    field.initialization.declaration.isNullable
                );
            }

            throw new SA_UnresolvedSymbolException(variable.variable); // LOG
        }
        if (expression instanceof FunctionCallNode functionCall) {
            FunctionDeclarationNode function = findMethodWithParameters(
                scope,
                functionCall.name,
                functionCall.arguments.stream().map(
                    argument -> new ExpressionTraverse().traverse(argument.value, scope)
                ).toList()
            );

            if (function != null) {
                return new TypeWrapper(function.returnType, false, function.isReturnTypeNullable);
            }

            throw new SA_UnresolvedSymbolException(functionCall.name); // LOG with parameters for more info
        }
        if (expression instanceof FieldReferenceNode field) {
            ClassDeclarationNode holder = scope.getClass(field.holderType);

            if (holder == null)
                throw new SA_UnresolvedSymbolException(field.holderType); // Log (unknown type)

            FieldNode actualField = holder.findField(scope, field.name);

            if (actualField == null)
                throw new SA_UnresolvedSymbolException(field.holderType + "." + field.name); // Log

            return new TypeWrapper(
                actualField.initialization.declaration.type,
                false,
                actualField.initialization.declaration.isNullable
            );
        }
        if (expression instanceof NullLiteral) {
            return new TypeWrapper("null", false, true);
        }

        throw new SA_SemanticError("Could not resolve type: '" + expression + "'"); // Log and return something
    }

    public record TypeWrapper(
        String type,
        boolean isTypeReference,
        boolean isNullable
    ) {
        @Override
        public String toString() {
            return type + (isNullable ? "?" : "");
        }
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
