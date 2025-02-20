package semantic_analysis.visitors;

import logger.LoggerFacade;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
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

import java.util.List;

import static semantic_analysis.loaders.SignatureLoader.*;

public class ExpressionTraverse {
    public FlowType traverse(ExpressionBaseNode root, Scope scope, boolean keepCompileTime) {
        if (!keepCompileTime)
            root.expression = transformValue(root, root.expression, scope);

        TypeWrapper type = determineType(root, root.expression, scope);
        if (type == null || type.isTypeReference) {
            LoggerFacade.error("Expression expected", root);
            return null;
        }

        return type.type;
    }

    public FlowType traverse(ExpressionBaseNode root, Scope scope) {
        return traverse(root, scope, false);
    }

    private static ExpressionNode transformValue(ExpressionBaseNode root, ExpressionNode expression, Scope scope) {
        if (expression instanceof VariableReferenceNode referenceNode) {
            return transformVariableReference(root, referenceNode, scope);
        }

        return transformOperators(root, expression, scope);
    }

    private static ExpressionNode transformOperators(ExpressionBaseNode root, ExpressionNode expression, Scope scope) {
        if (expression instanceof BinaryExpressionNode binaryExpression) {
            binaryExpression.left = transformValue(root, binaryExpression.left, scope);

            TypeWrapper leftType = determineType(root, binaryExpression.left, scope);
            if (leftType == null || leftType.type == null) {
                return null;
            }

            ClassDeclarationNode leftTypeNode = scope.getClass(leftType.type.name);

            if (leftTypeNode == null) {
                LoggerFacade.error("Unresolved symbol: '" + leftType.type + "'", root);
                return null;
            }

            if (binaryExpression.operator.equals(".")) {
                if (binaryExpression.right instanceof VariableReferenceNode reference) {
                    if (scope.findTypeDeclaration(reference.variable)) {
                        LoggerFacade.error("Cannot access nested types", root);
                        return null;
                    }

                    FieldNode field = leftTypeNode.findField(
                        scope,
                        reference.variable
                    );

                    if (field == null) {
                        LoggerFacade.error("Unresolved symbol: '" + leftType.type.name + "." + reference.variable + "'", root);
                        return null;
                    }

                    return new FieldReferenceNode(
                        leftType.type.name,
                        reference.variable,
                        binaryExpression.left,
                        field.initialization.declaration.type,
                        field.modifiers.contains("static")
                    );
                } else if (binaryExpression.right instanceof FunctionCallNode call) {
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
                            LoggerFacade.error("Unresolved symbol: '" + leftType.type + "." + call.name + "'", root);
                        } else {
                            LoggerFacade.error("None of the overrides for '" +
                                leftType.type + "." + call.name +
                                "' match the argument list", root);
                        }
                        return null;
                    }

                    return new FunctionCallNode(
                        leftType.type.name,
                        call.name,
                        call.arguments
                    );
                } else {
                    LoggerFacade.error("Expected field or function", root);
                    return null;
                }
            }

            if (leftType.isTypeReference) {
                LoggerFacade.error("Expression expected", root);
                return null;
            }

            binaryExpression.right = transformValue(root, binaryExpression.right, scope);

            TypeWrapper rightType = determineType(root, binaryExpression.right, scope);

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
                            ),
                            leftType.type
                        ),
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(
                                binaryExpression.right
                            ),
                            rightType.type
                        )
                    )
                )).findFirst().orElse(null);

            if (functionDecl != null) {
                return new FunctionCallNode(
                    leftType.type.name,
                    operatorName,
                    List.of(
                        new ArgumentNode(null, new ExpressionBaseNode(binaryExpression.left)),
                        new ArgumentNode(null, new ExpressionBaseNode(binaryExpression.right))
                    )
                );
            }

            LoggerFacade.error("Could not resolve operator '" + binaryExpression.operator + "' for '" + binaryExpression.left + "' and '" + binaryExpression.right + "'", root);
        } else if (expression instanceof UnaryOperatorNode unaryExpression) {
            unaryExpression.operand = transformValue(root, unaryExpression.operand, scope);

            TypeWrapper operandType = determineType(root, unaryExpression.operand, scope);

            if (operandType == null || operandType.isTypeReference) {
                LoggerFacade.error("Expression expected", root);
                return null;
            }

            ClassDeclarationNode operandTypeNode = scope.getClass(operandType.type.name);

            if (operandTypeNode == null) {
                LoggerFacade.error("Unresolved symbol: '" + operandType.type + "'", root);
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
                    operandType.type.name,
                    operatorName,
                    List.of(
                        new ArgumentNode(null, new ExpressionBaseNode(unaryExpression.operand))
                    )
                );
            }

            LoggerFacade.error("Could not resolve operator '" + unaryExpression.operator + "' for '", root);
        }

        return expression;
    }

    private static ExpressionNode transformVariableReference(ExpressionBaseNode root, VariableReferenceNode referenceNode, Scope scope) {
        if (
            scope.type() == Scope.Type.FUNCTION && scope.findLocalVariable(referenceNode.variable) ||
                scope.findTypeDeclaration(referenceNode.variable)
        ) {
            return referenceNode;
        }

        TypeDeclarationNode containingType = scope.getContainingType();
        FieldNode field = scope.getField(referenceNode.variable);

        if (field != null) {
            return new FieldReferenceNode(
                containingType != null ? containingType.name : null,
                field.initialization.declaration.name,
                new VariableReferenceNode("this"),
                field.initialization.declaration.type,
                field.modifiers.contains("static")
            );
        }

        LoggerFacade.error("Unresolved symbol: '" + referenceNode.variable + "'", root);
        return null;
    }

    private static TypeWrapper determineType(ExpressionBaseNode root, ExpressionNode expression, Scope scope) {
        if (expression == null) {
            return null;
        }
        if (expression instanceof ObjectNode objectNode) {
            if (!scope.findTypeDeclaration(objectNode.className)) {
                LoggerFacade.error("Unresolved symbol: '" + objectNode.className + "'", root);
                return null;
            }

            for (final ArgumentNode argNode : objectNode.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope, true);
            }

            return new TypeWrapper(
                new FlowType(
                    objectNode.className,
                    false,
                    false
                ),
                false
            );
        }
        if (expression instanceof BaseClassNode baseClassNode) {
            if (!scope.findTypeDeclaration(baseClassNode.name)) {
                LoggerFacade.error("Unresolved symbol: '" + baseClassNode.name + "'", root);
                return null;
            }

            for (final ArgumentNode argNode : baseClassNode.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope, true);
            }

            return new TypeWrapper(
                new FlowType(
                    baseClassNode.name,
                    false,
                    false
                ),
                false
            );
        }
        if (expression instanceof VariableReferenceNode variable) {
            if (scope.findTypeDeclaration(variable.variable)) {
                return new TypeWrapper(
                    new FlowType(
                        variable.variable,
                        false,
                        false
                    ),
                    true
                );
            }

            FieldNode field = scope.getField(variable.variable);

            if (field != null) {
                return new TypeWrapper(
                    field.initialization.declaration.type,
                    false
                );
            }

            LoggerFacade.error("Unresolved symbol: '" + variable.variable + "'", root);
            return null;
        }
        if (expression instanceof FunctionCallNode functionCall) {
            final FunctionDeclarationNode function;

            for (final ArgumentNode argNode : functionCall.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope);
            }

            if (functionCall.callerType != null) {
                final ClassDeclarationNode caller = scope.getClass(functionCall.callerType);

                if (caller == null) {
                    LoggerFacade.error("Unresolved symbol: '" + functionCall.callerType + "'", root);
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
                LoggerFacade.error("No overload found for: '" + functionCall.name + "', with the specified arguments", root);
                return null;
            }

            final TypeDeclarationNode containingType = scope.getContainingType();
            final String modifier = ModifierLoader.getAccessModifier(function.modifiers);

            if (modifier.equals("private") && (containingType == null || !containingType.name.equals(functionCall.callerType)) ||
                modifier.equals("protected") && (containingType == null || !scope.isSameType(
                    new FlowType(
                        containingType.name,
                        false,
                        false
                    ),
                    new FlowType(
                        functionCall.callerType,
                        false,
                        false
                    )
                ))
            ) {
                LoggerFacade.error("Cannot access '" + function.name + "', it is " + modifier + " in '" + functionCall.callerType + "'", root);
                return null;
            }

            return new TypeWrapper(function.returnType, false);
        }
        if (expression instanceof FieldReferenceNode field) {
            FieldNode actualField;
            ClassDeclarationNode holder = null;

            if (field.holderType != null) {
                holder = scope.getClass(field.holderType);

                if (holder == null) {
                    LoggerFacade.error("Unresolved symbol: '" + field.holderType + "'", root);
                    return null;
                }
                actualField = holder.findField(scope, field.name);
            } else {
                actualField = scope.getField(field.name);
            }

            if (actualField == null) {
                LoggerFacade.error("Unresolved symbol: '" + field.holderType + "." + field.name + "'", root);
                return null;
            }

            String modifier = ModifierLoader.getAccessModifier(actualField.modifiers);

            TypeDeclarationNode containingType = scope.getContainingType();

            if (holder != null)
                if (modifier.equals("private") && (containingType == null || !containingType.name.equals(holder.name)) ||
                    modifier.equals("protected") && (containingType == null || !scope.isSameType(
                        new FlowType(containingType.name, false, false),
                        new FlowType(holder.name, false, false)
                    ))
                ) {
                    LoggerFacade.error("Cannot access '" + field.name + "', it is " + modifier + " in '" + holder.name + "'", root);
                    return null;
                }

            return new TypeWrapper(
                actualField.initialization.declaration.type,
                false
            );
        }
        if (expression instanceof LiteralNode literalNode) {
            return new TypeWrapper(
                new FlowType(
                    literalNode.getClassName(),
                    false,
                    true
                ),
                false
            );
        }
        if (expression instanceof NullLiteral) {
            return new TypeWrapper(
                new FlowType(
                    "null",
                    true,
                    false
                ),
                false
            );
        }

        LoggerFacade.error("Could not resolve type: '" + expression + "'", root);
        return null;
    }

    public record TypeWrapper(
        FlowType type,
        boolean isTypeReference
    ) {
        @Override
        public String toString() {
            return type.toString();
        }
    }

    private static String getOperatorName(String operator) {
        return switch (operator) {
            case "+":
                yield "plus";
            case "-":
                yield "minus";
            case "*":
                yield "times";
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
