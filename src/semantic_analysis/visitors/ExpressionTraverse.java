package semantic_analysis.visitors;

import logger.Logger;
import logger.LoggerFacade;
import parser.nodes.ASTMetaDataStore;
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
import semantic_analysis.scopes.TypeRecognize;

import java.util.List;

import static semantic_analysis.visitors.ParameterTraverse.*;

public class ExpressionTraverse {
    public FlowType traverse(ExpressionBaseNode root, Scope scope) {
        root.expression = transformValue(root, root.expression, scope);

        return determineType(root, root.expression, scope);
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

            FlowType leftType = determineType(root, binaryExpression.left, scope);
            if (leftType == null) {
                return null;
            }

            TypeDeclarationNode leftTypeNode = TypeRecognize.getTypeDeclaration(leftType.name, scope);

            if (leftTypeNode == null) {
                LoggerFacade.error("Unresolved symbol: '" + leftType + "'", root);
                return null;
            }

            if (binaryExpression.operator.equals(".") || binaryExpression.operator.equals("?.")) {
                if (binaryExpression.operator.equals(".") && leftType.isNullable) {
                    LoggerFacade.error("Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type '" + leftType + "'", root);
                }

                if (binaryExpression.right instanceof VariableReferenceNode reference) {
                    if (leftTypeNode instanceof ClassDeclarationNode leftTypeClass) {
                        if (TypeRecognize.findTypeDeclaration(reference.variable, scope)) {
                            LoggerFacade.error("Cannot access nested types", root);
                            return null;
                        }

                        FieldNode field = leftTypeClass.findField(
                            scope,
                            reference.variable
                        );

                        if (field == null) {
                            LoggerFacade.error("Unresolved symbol: '" + leftType.name + "." + reference.variable + "'", root);
                            return null;
                        }

                        return new FieldReferenceNode(
                            leftType,
                            reference.variable,
                            binaryExpression.left,
                            field.initialization.declaration.type,
                            field.modifiers.contains("static")
                        );
                    }

                    LoggerFacade.error("Unresolved reference '" + binaryExpression.right + "'", root);
                } else if (binaryExpression.right instanceof FunctionCallNode call) {
                    List<FunctionDeclarationNode> functions = leftTypeNode.findMethodsWithName(
                        scope,
                        call.name
                    );

                    for (final ArgumentNode argNode : call.arguments) {
                        argNode.type = new ExpressionTraverse().traverse(argNode.value, scope);
                    }

                    FunctionDeclarationNode function = findMethodByArguments(
                        scope,
                        functions,
                        call.name,
                        call.arguments,
                        leftType
                    );

                    if (function == null) {
                        if (functions.isEmpty()) {
                            LoggerFacade.error("Unresolved symbol: '" + leftType + "." + call.name + "'", root);
                        } else {
                            LoggerFacade.error("None of the overrides for '" +
                                leftType + "." + call.name +
                                "' match the argument list", root);
                        }
                        return null;
                    }

                    return new FunctionCallNode(
                        leftType,
                        binaryExpression.left instanceof TypeReferenceNode ? null : binaryExpression.left,
                        binaryExpression.operator.equals("?."),
                        call.name,
                        call.arguments
                    );
                } else {
                    LoggerFacade.error("Expected field or function", root);
                    return null;
                }
            }

            if (binaryExpression.left instanceof TypeReferenceNode) {
                LoggerFacade.error("Expression expected", root);
                return null;
            }

            binaryExpression.right = transformValue(root, binaryExpression.right, scope);

            FlowType rightType = determineType(root, binaryExpression.right, scope);

            if (rightType == null || binaryExpression.right instanceof TypeReferenceNode) {
                LoggerFacade.error("Expression expected", root);
                return null;
            }

            String operatorName = getOperatorName(binaryExpression.operator);

            final List<FunctionDeclarationNode> functions = leftTypeNode.findMethodsWithName(
                scope,
                operatorName
            );

            FunctionDeclarationNode functionDecl = functions.stream()
                .filter(method -> ParameterTraverse.compareParametersWithArguments(
                    scope,
                    method.parameters,
                    List.of(
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(
                                binaryExpression.right
                            ),
                            rightType
                        )
                    ),
                    leftType
                )).findFirst().orElse(null);

            if (functionDecl != null) {
                return new FunctionCallNode(
                    leftType,
                    binaryExpression.left,
                    false,
                    operatorName,
                    List.of(
                        new ArgumentNode(null, new ExpressionBaseNode(binaryExpression.right))
                    )
                );
            }

            LoggerFacade.error("Could not resolve operator '" + binaryExpression.operator + "' for '" + binaryExpression.left + "' and '" + binaryExpression.right + "'", root);
        } else if (expression instanceof UnaryOperatorNode unaryExpression) {
            unaryExpression.operand = transformValue(root, unaryExpression.operand, scope);

            FlowType operandType = determineType(root, unaryExpression.operand, scope);

            if (operandType == null || unaryExpression.operand instanceof TypeReferenceNode) {
                LoggerFacade.error("Expression expected", root);
                return null;
            }

            unaryExpression.operandType = new FlowType(
                operandType.name,
                operandType.isNullable,
                operandType.isPrimitive
            );

            if (getUnaryOperatorType(unaryExpression.operator) != UnaryOperatorType.FUNCTION) {
                return unaryExpression;
            }

            if (unaryExpression.operator.equals("!!")) {
                if (!unaryExpression.operandType.isNullable) {
                    LoggerFacade.getLogger().log(
                        Logger.Severity.WARNING,
                        "'!!' operator used on a non-nullable type",
                        ASTMetaDataStore.getInstance().getLine(root),
                        ASTMetaDataStore.getInstance().getFile(root)
                    );
                }

                unaryExpression.operandType.isNullable = false;

                return unaryExpression;
            }

            ClassDeclarationNode operandTypeNode = TypeRecognize.getClass(operandType.name, scope);

            if (operandTypeNode == null) {
                LoggerFacade.error("Unresolved symbol: '" + operandType + "'", root);
                return null;
            }

            String operatorName = getUnaryOperatorName(unaryExpression.operator, unaryExpression.isPostfix);

            final List<FunctionDeclarationNode> functions = operandTypeNode.findMethodsWithName(
                scope,
                operatorName
            );

            FunctionDeclarationNode functionDecl = functions.stream()
                .filter(method -> ParameterTraverse.compareParametersWithArguments(
                    scope,
                    method.parameters,
                    List.of(),
                    operandType
                )).findFirst().orElse(null);

            if (functionDecl != null) {
                return new FunctionCallNode(
                    operandType,
                    unaryExpression.operand,
                    false,
                    operatorName,
                    List.of()
                );
            }

            LoggerFacade.error("Could not resolve operator '" + unaryExpression.operator + "'", root);
        }

        return expression;
    }

    private static ExpressionNode transformVariableReference(ExpressionBaseNode root, VariableReferenceNode referenceNode, Scope scope) {
        if (scope.type() == Scope.Type.FUNCTION && scope.findLocalVariable(referenceNode.variable))
            return referenceNode;

        if (TypeRecognize.findTypeDeclaration(referenceNode.variable, scope)) {
            return new TypeReferenceNode(
                new FlowType(
                    referenceNode.variable,
                    false,
                    false
                )
            );
        }

        TypeDeclarationNode containingType = scope.getContainingType();
        FieldNode field = TypeRecognize.getField(referenceNode.variable, scope);

        if (field != null) {
            return new FieldReferenceNode(
                containingType != null ? new FlowType(containingType.name, false, false) : null,
                field.initialization.declaration.name,
                new VariableReferenceNode("this"),
                field.initialization.declaration.type,
                field.modifiers.contains("static")
            );
        }

        LoggerFacade.error("Unresolved symbol: '" + referenceNode.variable + "'", root);
        return null;
    }

    private static FlowType determineType(ExpressionBaseNode root, ExpressionNode expression, Scope scope) {
        if (expression == null) {
            return null;
        }
        if (expression instanceof TypeReferenceNode typeReference) {
            return typeReference.type;
        }
        if (expression instanceof ObjectNode objectNode) {
            if (objectNode.type.isNullable) {
                LoggerFacade.warning("The nullable type indicator ('?') has no effect when instantiating an object and will be ignored");
                objectNode.type.isNullable = false;
            }

            TypeDeclarationNode baseType = TypeRecognize.getTypeDeclaration(objectNode.type.name, scope);

            if (baseType == null) {
                LoggerFacade.error("Unresolved symbol: '" + objectNode.type.name + "'", root);
                return null;
            }

            if (baseType instanceof InterfaceNode) {
                LoggerFacade.error("Type '" + objectNode.type.name + "' does not have a constructor", root);
                return null;
            }

            ClassDeclarationNode baseClass = (ClassDeclarationNode) baseType;

            for (final ArgumentNode argNode : objectNode.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope);
            }

            if (findConstructor(scope, baseClass.constructors, objectNode.arguments, objectNode.type) == null) {
                LoggerFacade.error("No matching constructor found for the specified arguments", root);
                return null;
            }

            if (!compareTypeParameters(scope, baseType.typeParameters, objectNode.type.typeArguments)) {
                LoggerFacade.error("Type arguments does not match type parameters, actual: '" + objectNode.type + "', expected: '" + baseType.typeParameters + "'", root);
                return null;
            }

            return objectNode.type;
        }
        if (expression instanceof BaseClassNode baseClassNode) {
            if (baseClassNode.type.isNullable) {
                LoggerFacade.error("Cannot extend nullable type '" + baseClassNode.type + "'", root);
                return null;
            }

            TypeDeclarationNode baseType = TypeRecognize.getTypeDeclaration(baseClassNode.type.name, scope);

            if (baseType == null) {
                LoggerFacade.error("Unresolved symbol: '" + baseClassNode.type.name + "'", root);
                return null;
            }

            if (baseType instanceof InterfaceNode) {
                LoggerFacade.error("Type '" + baseClassNode.type.name + "' does not have a constructor", root);
                return null;
            }

            ClassDeclarationNode baseClass = (ClassDeclarationNode) baseType;

            for (final ArgumentNode argNode : baseClassNode.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope);
            }

            if (findConstructor(scope, baseClass.constructors, baseClassNode.arguments, baseClassNode.type) == null) {
                LoggerFacade.error("No matching constructor found for the specified arguments", root);
                return null;
            }

            return baseClassNode.type;
        }
        if (expression instanceof VariableReferenceNode variable) {
            FieldNode field = TypeRecognize.getField(variable.variable, scope);

            if (field != null) {
                return field.initialization.declaration.type;
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
                final TypeDeclarationNode caller = TypeRecognize.getTypeDeclaration(functionCall.callerType.name, scope);

                if (caller == null) {
                    LoggerFacade.error("Unresolved symbol: '" + functionCall.callerType + "'", root);
                    return null;
                }

                final List<FunctionDeclarationNode> functions = caller.findMethodsWithName(
                    scope,
                    functionCall.name
                );

                function = functions.stream()
                    .filter(method -> ParameterTraverse.compareParametersWithArguments(
                        scope,
                        method.parameters,
                        functionCall.arguments,
                        functionCall.callerType
                    )).findFirst().orElse(null);
            } else {
                function = ParameterTraverse.findMethodByArguments(
                    scope,
                    functionCall.name,
                    functionCall.arguments,
                    null
                );
            }

            if (function == null) {
                LoggerFacade.error("No overload found for: '" + functionCall.name + "', with the specified arguments", root);
                return null;
            }

            final TypeDeclarationNode containingType = scope.getContainingType();
            final String modifier = ModifierLoader.getAccessModifier(function.modifiers);

            if (modifier.equals("private") && (containingType == null || !containingType.name.equals(functionCall.callerType.name)) ||
                modifier.equals("protected") && (containingType == null || !TypeRecognize.isSameType(
                    new FlowType(
                        containingType.name,
                        false,
                        false
                    ),
                    functionCall.callerType,
                    scope
                ))
            ) {
                LoggerFacade.error("Cannot access '" + function.name + "', it is " + modifier + " in '" + functionCall.callerType + "'", root);
                return null;
            }

            return !functionCall.isSafeCall ? function.returnType : new FlowType(function.returnType.name, true, false);
        }
        if (expression instanceof FieldReferenceNode field) {
            FieldNode actualField;
            ClassDeclarationNode holder = null;

            if (field.holderType != null) {
                holder = TypeRecognize.getClass(field.holderType.name, scope);

                if (holder == null) {
                    LoggerFacade.error("Unresolved symbol: '" + field.holderType + "'", root);
                    return null;
                }
                actualField = holder.findField(scope, field.name);
            } else {
                actualField = TypeRecognize.getField(field.name, scope);
            }

            if (actualField == null) {
                LoggerFacade.error("Unresolved symbol: '" + field.holderType + "." + field.name + "'", root);
                return null;
            }

            String modifier = ModifierLoader.getAccessModifier(actualField.modifiers);

            TypeDeclarationNode containingType = scope.getContainingType();

            if (holder != null)
                if (modifier.equals("private") && (containingType == null || !containingType.name.equals(holder.name)) ||
                    modifier.equals("protected") && (containingType == null || !TypeRecognize.isSameType(
                        new FlowType(containingType.name, false, false),
                        new FlowType(holder.name, false, false),
                        scope
                    ))
                ) {
                    LoggerFacade.error("Cannot access '" + field.name + "', it is " + modifier + " in '" + holder.name + "'", root);
                    return null;
                }

            return actualField.initialization.declaration.type;
        }
        if (expression instanceof UnaryOperatorNode unaryExpression) {
            if (getUnaryOperatorType(unaryExpression.operator) == UnaryOperatorType.MUTATING) {
                String name = null;
                if (unaryExpression.operand instanceof VariableReferenceNode variableReferenceNode) {
                    name = variableReferenceNode.variable;
                } else if (unaryExpression.operand instanceof FieldReferenceNode fieldReferenceNode) {
                    name = fieldReferenceNode.name;
                }

                if (name == null) {
                    LoggerFacade.error("Variable expected with '" + unaryExpression.operator + "'", root);
                    return null;
                }

                FieldNode field = TypeRecognize.getField(name, scope);
                if (field == null) {
                    return null;
                }

                if (!field.initialization.declaration.modifier.equals("var")) {
                    LoggerFacade.error(field.initialization.declaration.modifier + " cannot be reassigned", root);
                }
            }

            return unaryExpression.operandType;
        }
        if (expression instanceof LiteralNode literalNode) {
            return new FlowType(
                literalNode.getClassName(),
                false,
                true
            );
        }
        if (expression instanceof NullLiteral) {
            return new FlowType(
                "null",
                true,
                false
            );
        }

        LoggerFacade.error("Could not resolve type: '" + expression + "'", root);
        return null;
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
            case "[]":
                yield "get";
            default:
                yield null;
        };
    }

    private static String getUnaryOperatorName(String operator, boolean isPostfix) {
        return switch (operator) {
            case "+":
                yield "pos";
            case "-":
                yield "neg";
            case "!":
                yield "not";
            case "++":
                if (isPostfix)
                    yield "postInc";
                else
                    yield "preInc";
            case "--":
                if (isPostfix)
                    yield "postDec";
                else
                    yield "preDec";
            default:
                yield null;
        };
    }

    private static UnaryOperatorType getUnaryOperatorType(String operator) {
        return switch (operator) {
            case "++", "--":
                yield UnaryOperatorType.MUTATING;
            default:
                yield UnaryOperatorType.FUNCTION;
        };
    }

    public enum UnaryOperatorType {
        MUTATING,
        FUNCTION
    }
}
