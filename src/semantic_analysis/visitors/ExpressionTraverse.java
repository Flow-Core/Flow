package semantic_analysis.visitors;

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
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.loaders.ModifierLoader;
import semantic_analysis.scopes.Scope;
import semantic_analysis.transformers.LiteralTransformer;

import java.util.List;

import static semantic_analysis.loaders.SignatureLoader.*;

public class ExpressionTraverse {
    public TypeWrapper traverse(ExpressionBaseNode root, Scope scope, boolean keepCompileTime) {
        if (!keepCompileTime)
            root.expression = transformValue(root.expression, scope);

        TypeWrapper type = determineType(root.expression, scope);

        if (type.isTypeReference)
            throw new SA_SemanticError("Expression expected"); // Log

        return type;
    }

    public TypeWrapper traverse(ExpressionBaseNode root, Scope scope) {
        return traverse(root, scope, false);
    }

    private static ExpressionNode transformValue(ExpressionNode expression, Scope scope) {
        if (expression instanceof LiteralNode literalNode) {
            return LiteralTransformer.transform(literalNode);
        }
        if (expression instanceof VariableReferenceNode referenceNode) {
            return transformVariableReference(referenceNode, scope);
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
                        binaryExpression.left,
                        new TypeWrapper(
                            field.initialization.declaration.type,
                            false,
                            field.initialization.declaration.isNullable
                        ),
                        field.modifiers.contains("static")
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
                        if (functions.isEmpty())
                            throw new SA_UnresolvedSymbolException(leftType.type + "." + call.name); // Log
                        else
                            throw new SA_SemanticError("None of the overrides for '" +
                                leftType.type + "." + call.name +
                                "' match the argument list");
                    }

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
                            leftType
                        ),
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(
                                binaryExpression.right
                            ),
                            rightType
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

            throw new SA_SemanticError("Could not resolve operator '" + binaryExpression.operator + "' for '" + binaryExpression.left + "' and '" + binaryExpression.right + "'");
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

            throw new SA_SemanticError("Could not resolve operator '" + unaryExpression.operator + "' for '" + unaryExpression.operand);
        }

        return expression;
    }

    private static ExpressionNode transformVariableReference(VariableReferenceNode referenceNode, Scope scope) {
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
                new TypeWrapper(
                    field.initialization.declaration.type,
                    false,
                    field.initialization.declaration.isNullable
                ),
                field.modifiers.contains("static")
            );
        }

        throw new SA_UnresolvedSymbolException(referenceNode.variable); // LOG
    }

    private static TypeWrapper determineType(ExpressionNode expression, Scope scope) {
        if (expression instanceof ObjectNode objectNode) {
            if (!scope.findTypeDeclaration(objectNode.className))
                throw new SA_UnresolvedSymbolException(objectNode.className); //Log

            for (final ArgumentNode argNode : objectNode.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope, true);
            }

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
            final FunctionDeclarationNode function;

            for (final ArgumentNode argNode : functionCall.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope);
            }

            if (functionCall.callerType != null) {
                final ClassDeclarationNode caller = scope.getClass(functionCall.callerType);

                if (caller == null) throw new SA_UnresolvedSymbolException(functionCall.callerType);

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

            if (function == null)
                throw new SA_UnresolvedSymbolException(functionCall.name); // LOG with parameters for more info

            final TypeDeclarationNode containingType = scope.getContainingType();
            final String modifier = ModifierLoader.getAccessModifier(function.modifiers);

            if (modifier.equals("private") && (containingType == null || !containingType.name.equals(functionCall.callerType)) ||
                modifier.equals("protected") && (containingType == null || !scope.isSameType(
                    new TypeWrapper(containingType.name, false, false),
                    new TypeWrapper(functionCall.callerType, false, false)
                ))
            ) throw new SA_SemanticError("Cannot access '" + function.name + "', it is " + modifier + " in '" + functionCall.callerType + "'");

            return new TypeWrapper(function.returnType, false, function.isReturnTypeNullable);
        }
        if (expression instanceof FieldReferenceNode field) {
            FieldNode actualField;
            ClassDeclarationNode holder = null;

            if (field.holderType != null) {
                holder = scope.getClass(field.holderType);

                if (holder == null)
                    throw new SA_UnresolvedSymbolException(field.holderType); // Log (unknown type)
                actualField = holder.findField(scope, field.name);
            } else {
                actualField = scope.getField(field.name);
            }

            if (actualField == null)
                throw new SA_UnresolvedSymbolException(field.holderType + "." + field.name); // Log

            String modifier = ModifierLoader.getAccessModifier(actualField.modifiers);

            TypeDeclarationNode containingType = scope.getContainingType();

            if (holder != null)
                if (modifier.equals("private") && (containingType == null || !containingType.name.equals(holder.name)) ||
                    modifier.equals("protected") && (containingType == null || !scope.isSameType(
                        new TypeWrapper(containingType.name, false, false),
                        new TypeWrapper(holder.name, false, false)
                    ))
                ) throw new SA_SemanticError("Cannot access '" + field.name + "', it is " + modifier + " in '" + holder.name + "'");

            return new TypeWrapper(
                actualField.initialization.declaration.type,
                false,
                actualField.initialization.declaration.isNullable
            );
        }
        if (expression instanceof LiteralNode literalNode) {
            return new TypeWrapper(literalNode.getClassName(), false, false);
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
