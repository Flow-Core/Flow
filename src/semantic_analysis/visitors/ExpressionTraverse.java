package semantic_analysis.visitors;

import logger.LoggerFacade;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.ObjectNode;
import parser.nodes.classes.TypeDeclarationNode;
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
import semantic_analysis.loaders.ModifierLoader;
import semantic_analysis.scopes.Scope;
import semantic_analysis.transformers.LiteralTransformer;

import java.util.List;

import static semantic_analysis.loaders.SignatureLoader.*;

public class ExpressionTraverse {
    public TypeWrapper traverse(ExpressionBaseNode root, Scope scope) {
        root.expression = transformValue(root.expression, scope);

        TypeWrapper type = determineType(root.expression, scope);
        if (type == null || type.isTypeReference) {
            LoggerFacade.error("Expression expected", root.expression);
            return null;
        }

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
            if (leftType == null) {
                return null;
            }

            ClassDeclarationNode leftTypeNode = scope.getClass(leftType.type);

            if (leftTypeNode == null) {
                LoggerFacade.error("Unresolved symbol: '" + leftType.type + "'", expression);
                return null;
            }

            if (binaryExpression.operator.equals(".")) {
                if (binaryExpression.right instanceof VariableReferenceNode reference) {
                    if (scope.findTypeDeclaration(reference.variable)) {
                        LoggerFacade.error("Cannot access nested types", expression);
                        return null;
                    }

                    FieldNode field = leftTypeNode.findField(
                        scope,
                        reference.variable
                    );

                    if (field == null) {
                        LoggerFacade.error("Unresolved symbol: '" + leftType.type + "." + reference.variable + "'", expression);
                        return null;
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

                    FunctionDeclarationNode function = findMethodWithParameters(
                        scope,
                        functions,
                        call.name,
                        call.arguments.stream().map(
                            argument -> new ExpressionTraverse().traverse(argument.value, scope)
                        ).toList()
                    );

                    if (function == null) {
                        if (functions.isEmpty()) {
                            LoggerFacade.error("Unresolved symbol: '" + leftType.type + "." + call.name + "'", expression);
                        } else {
                            LoggerFacade.error("None of the overrides for '" +
                                leftType.type + "." + call.name +
                                "' match the argument list", expression);
                        }
                        return null;
                    }

                    return new FunctionCallNode(
                        leftType.type,
                        call.name,
                        call.arguments
                    );
                } else {
                    LoggerFacade.error("Expected field or function", expression);
                    return null;
                }
            }

            if (leftType.isTypeReference) {
                LoggerFacade.error("Expression expected", expression);
                return null;
            }

            binaryExpression.right = transformValue(binaryExpression.right, scope);

            TypeWrapper rightType = determineType(binaryExpression.right, scope);

            if (rightType == null || rightType.isTypeReference) {
                LoggerFacade.error("Expression expected", expression);
                return null;
            }

            String operatorName = getOperatorName(binaryExpression.operator);

            final List<FunctionDeclarationNode> functions = leftTypeNode.findMethodsWithName(
                scope,
                operatorName
            );

            FunctionDeclarationNode functionDecl = functions.stream()
                .filter(method -> compareParameterTypes(
                    scope,
                    method.parameters,
                    List.of(
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(
                                binaryExpression.left
                            )
                        ),
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(
                                binaryExpression.right
                            )
                        )
                    )
                )).findFirst().orElse(null);

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

            if (operandType == null || operandType.isTypeReference) {
                LoggerFacade.error("Expression expected", expression);
                return null;
            }

            ClassDeclarationNode operandTypeNode = scope.getClass(operandType.type);

            if (operandTypeNode == null) {
                LoggerFacade.error("Unresolved symbol: '" + operandType.type + "'", expression);
                return null;
            }

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
        if (expression == null) {
            return null;
        }
        if (expression instanceof ObjectNode objectNode) {
            if (!scope.findTypeDeclaration(objectNode.className))
                throw new SA_UnresolvedSymbolException(objectNode.className); //Log

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

            LoggerFacade.error("Unresolved symbol: '" + variable.variable + "'", expression);
            return null;
        }
        if (expression instanceof FunctionCallNode functionCall) {
            final FunctionDeclarationNode function;

            if (functionCall.callerType != null) {
                final ClassDeclarationNode caller = scope.getClass(functionCall.callerType);

                if (caller == null) {
                    LoggerFacade.error("Unresolved symbol: '" + functionCall.callerType + "'", expression);
                    return null;
                }

                final List<FunctionDeclarationNode> functions = caller.findMethodsWithName(
                    scope,
                    functionCall.name
                );

                function = functions.stream()
                    .filter(method -> compareParameterTypes(
                        scope,
                        method.parameters,
                        functionCall.arguments
                    )).findFirst().orElse(null);
            } else {
                function = findMethodByArguments(
                    scope,
                    functionCall.name,
                    functionCall.arguments
                );
            }

            if (function == null) {
                LoggerFacade.error("No overload found for: '" + functionCall.name + "', with the specified arguments", expression);
                return null;
            }

            final TypeDeclarationNode containingType = scope.getContainingType();
            final String modifier = ModifierLoader.getAccessModifier(function.modifiers);

            if (modifier.equals("private") && (containingType == null || !containingType.name.equals(functionCall.callerType)) ||
                modifier.equals("protected") && (containingType == null || !scope.isSameType(
                    new TypeWrapper(containingType.name, false, false),
                    new TypeWrapper(functionCall.callerType, false, false)
                ))
            ) {
                LoggerFacade.error("Cannot access '" + function.name + "', it is " + modifier + " in '" + functionCall.callerType + "'", expression);
                return null;
            }

            return new TypeWrapper(function.returnType, false, function.isReturnTypeNullable);
        }
        if (expression instanceof FieldReferenceNode field) {
            ClassDeclarationNode holder = scope.getClass(field.holderType);

            if (holder == null) {
                LoggerFacade.error("Unresolved symbol: '" + field.holderType + "'", expression);
                return null;
            }

            FieldNode actualField = holder.findField(scope, field.name);

            if (actualField == null) {
                LoggerFacade.error("Unresolved symbol: '" + field.holderType + "." + field.name + "'", expression);
                return null;
            }

            String modifier = ModifierLoader.getAccessModifier(actualField.modifiers);

            TypeDeclarationNode containingType = scope.getContainingType();

            if (modifier.equals("private") && (containingType == null || !containingType.name.equals(holder.name)) ||
                modifier.equals("protected") && (containingType == null || !scope.isSameType(
                    new TypeWrapper(containingType.name, false, false),
                    new TypeWrapper(holder.name, false, false)
                ))
            ) {
                LoggerFacade.error("Cannot access '" + field.name + "', it is " + modifier + " in '" + holder.name + "'", expression);
                return null;
            }

            return new TypeWrapper(
                actualField.initialization.declaration.type,
                false,
                actualField.initialization.declaration.isNullable
            );
        }
        if (expression instanceof NullLiteral) {
            return new TypeWrapper("null", false, true);
        }

        LoggerFacade.error("Could not resolve type: '" + expression + "'", expression);
        return null;
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
