package semantic_analysis.visitors;

import logger.Logger;
import logger.LoggerFacade;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.*;
import parser.nodes.expressions.networking.ConnectionNode;
import parser.nodes.expressions.networking.StartNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.functions.LambdaExpressionNode;
import parser.nodes.functions.MethodReferenceNode;
import parser.nodes.generics.TypeArgument;
import parser.nodes.literals.LiteralNode;
import parser.nodes.literals.NullLiteral;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.loaders.FunctionLoader;
import semantic_analysis.loaders.ModifierLoader;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if (expression instanceof LambdaExpressionNode lambdaExpressionNode) {
            return transformLambda(lambdaExpressionNode, scope);
        }
        if (expression instanceof FunctionCallNode functionCallNode) {
            return transformFunctionCall(root, functionCallNode, scope);
        }
        if (expression instanceof StartNode startNode) {
            return transformStart(root, startNode, scope);
        }

        return transformOperators(root, expression, scope);
    }

    private static ExpressionNode transformOperators(ExpressionBaseNode root, ExpressionNode expression, Scope scope) {
        if (expression instanceof BinaryExpressionNode binaryExpression) {
            return transformBinaryOperator(
                root,
                binaryExpression,
                scope
            );
        } else if (expression instanceof UnaryOperatorNode unaryExpression) {
            return transformUnaryOperator(
                root,
                unaryExpression,
                scope
            );
        }

        return expression;
    }

    private static ExpressionNode transformBinaryOperator(
        ExpressionBaseNode root,
        BinaryExpressionNode binaryExpression,
        Scope scope
    ) {
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

        if (binaryExpression.operator.equals(".") || binaryExpression.operator.equals("?.") || binaryExpression.operator.equals("::")) {
            return transformDotOperator(
                root,
                binaryExpression.operator,
                leftType,
                leftTypeNode,
                binaryExpression.left,
                binaryExpression.right,
                scope
            );
        }

        if (binaryExpression.left instanceof TypeReferenceNode) {
            LoggerFacade.error("Expression expected", root);
            return null;
        }

        binaryExpression.right = transformValue(root, binaryExpression.right, scope);

        FlowType rightType = determineType(root, binaryExpression.right, scope);

        switch (binaryExpression.operator) {
            case ":" -> {
                return transformAddress(
                    root,
                    binaryExpression.left,
                    leftType,
                    binaryExpression.right,
                    rightType,
                    scope
                );
            }
            case "~" -> {
                return transformConnection(
                    root,
                    binaryExpression.left,
                    leftType,
                    binaryExpression.right,
                    rightType,
                    scope
                );
            }
            case "as" -> {
                return transformCast(root, binaryExpression);
            }
            case "is", "is not" -> {
                return transformIs(root, binaryExpression);
            }
        }

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
        return null;
    }

    private static ExpressionNode transformUnaryOperator(
        ExpressionBaseNode root,
        UnaryOperatorNode unaryExpression,
        Scope scope
    ) {
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
        return null;
    }

    private static ExpressionNode transformDotOperator(
        ExpressionBaseNode root,
        String operator,
        FlowType leftType,
        TypeDeclarationNode leftTypeNode,
        ExpressionNode leftExpr,
        ExpressionNode rightExpr,
        Scope scope
    ) {
        if (!leftType.isExternalType && operator.equals(".") && leftType.isNullable) {
            LoggerFacade.error("Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type '" + leftType + "'", root);
        }

        if (rightExpr instanceof VariableReferenceNode reference) {
            if (leftTypeNode instanceof ClassDeclarationNode leftTypeClass) {
                if (TypeRecognize.findTypeDeclaration(reference.variable, scope)) {
                    return new TypeReferenceNode(
                        FlowType.of(leftType.name + "." + reference.variable)
                    );
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
                    leftExpr,
                    field.initialization.declaration.type,
                    field,
                    field.modifiers.contains("static")
                );
            }

            LoggerFacade.error("Unresolved reference '" + rightExpr + "'", root);
        } else if (rightExpr instanceof FunctionCallNode call) {
            List<FunctionDeclarationNode> functions = leftTypeNode.findMethodsWithName(
                scope,
                call.name
            );

            for (final ArgumentNode argNode : call.arguments) {
                argNode.type = new ExpressionTraverse().traverse(argNode.value, scope);

                if (!operator.equals("::") && argNode.value.expression instanceof TypeReferenceNode) {
                    LoggerFacade.error("Expression expected", root);
                }
                if (operator.equals("::") && !(argNode.value.expression instanceof TypeReferenceNode)) {
                    LoggerFacade.error("Expected parameter type", root);
                }
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

            if (operator.equals("::")) {
                return new MethodReferenceNode(
                    leftType,
                    function
                );
            }

            return new FunctionCallNode(
                leftType,
                leftExpr,
                operator.equals("?."),
                call.name,
                call.arguments
            );
        }

        LoggerFacade.error("Expected field or function", root);
        return null;
    }

    private static ExpressionNode transformAddress(ExpressionBaseNode root, ExpressionNode ip, FlowType ipType, ExpressionNode port, FlowType portType, Scope scope) {
        if (!TypeRecognize.isSameType(
            ipType,
            FlowType.of("flow.Ip"),
            scope
        )) {
            LoggerFacade.error("Left side of an address declaration must be of type 'Ip'", root);
            return null;
        }

        if (!TypeRecognize.isSameType(
            portType,
            FlowType.primitive("flow.Int"),
            scope
        )) {
            LoggerFacade.error("Right side of an address declaration must be of type 'Int'", root);
            return null;
        }

        return new ObjectNode(
            FlowType.of("flow.Address"),
            List.of(
                new ArgumentNode(
                    null,
                    new ExpressionBaseNode(
                        ip
                    )
                ),
                new ArgumentNode(
                    null,
                    new ExpressionBaseNode(
                        port
                    )
                )
            )
        );
    }

    private static ExpressionNode transformConnection(ExpressionBaseNode root, ExpressionNode address, FlowType addressType, ExpressionNode protocol, FlowType protocolType, Scope scope) {
        if (!TypeRecognize.isSameType(
            addressType,
            FlowType.of("flow.Address"),
            scope
        )) {
            LoggerFacade.error("Left side of a connection declaration must be of type 'Address'", root);
            return null;
        }

        if (!(protocol instanceof TypeReferenceNode)) {
            LoggerFacade.error("Right side of a connection declaration must be a type reference", root);
            return null;
        }

        if (!TypeRecognize.isSameType(
            protocolType,
            FlowType.of("flow.networking.Protocol"),
            scope
        )) {
            LoggerFacade.error("Protocol class must be a subtype of 'Protocol'", root);
            return null;
        }

        return new ObjectNode( // new Socket(address, PType::encode(PType, OutputStream), PType::decode(InputStream))
            FlowType.of(
                "flow.networking.Socket",
                List.of(
                    new TypeArgument(
                        protocolType
                    )
                )
            ),
            List.of(
                new ArgumentNode(null, new ExpressionBaseNode(address), addressType),
                getEncodeArg(root, protocolType, scope),
                getDecodeArg(root, protocolType, scope)
            )
        );
    }

    private static ExpressionNode transformStart(ExpressionBaseNode root, StartNode startNode, Scope scope) {
        ClassDeclarationNode serverTypeNode = TypeRecognize.getClass(
            startNode.serverType.name,
            scope
        );

        if (serverTypeNode == null) {
            LoggerFacade.error("Unresolved type: '" + startNode.serverType + "'", root);
            return null;
        }

        if (serverTypeNode.baseClasses.isEmpty() || !serverTypeNode.baseClasses.get(0).type.name.equals("flow.networking.Server") && !Objects.equals(scope.getFQName(serverTypeNode.baseClasses.get(0)), "flow.networking.Server")) {
            LoggerFacade.error("'" + startNode.serverType + "' does not inherit from 'Server'. Only server types can be started", root);
            return null;
        }

        FlowType protocolType = serverTypeNode.baseClasses.get(0).type.typeArguments.get(0).type;

        FlowType portType = new ExpressionTraverse().traverse(startNode.port, scope);

        if (!TypeRecognize.isSameType(
            portType,
            FlowType.primitive("flow.Int"),
            scope
        )) {
            LoggerFacade.error("Port must be of type 'flow.Int'", root);
        }

        return transformValue(
            root,
            new BinaryExpressionNode(
                new ObjectNode(
                    startNode.serverType,
                    List.of(
                        new ArgumentNode(
                            null, startNode.port
                        ),
                        getEncodeArg(root, protocolType, scope),
                        getDecodeArg(root, protocolType, scope)
                    )
                ),
                new FunctionCallNode(
                    "start",
                    List.of()
                ),
                "."
            ),
            scope
        );
    }

    private static ArgumentNode getEncodeArg(ExpressionBaseNode root, FlowType protocolType, Scope scope) {
        return new ArgumentNode(null, new ExpressionBaseNode(transformValue(
            root,
            new BinaryExpressionNode(
                new TypeReferenceNode(protocolType),
                new FunctionCallNode(
                    "encode",
                    List.of(
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(new TypeReferenceNode(protocolType))
                        ),
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(new TypeReferenceNode(
                                FlowType.of("java.io.OutputStream")
                            ))
                        )
                    )
                ),
                "::"
            ),
            scope
        )));
    }

    private static ArgumentNode getDecodeArg(ExpressionBaseNode root, FlowType protocolType, Scope scope) {
        return new ArgumentNode(null, new ExpressionBaseNode(transformValue(
            root,
            new BinaryExpressionNode(
                new TypeReferenceNode(protocolType),
                new FunctionCallNode(
                    "decode",
                    List.of(
                        new ArgumentNode(
                            null,
                            new ExpressionBaseNode(new TypeReferenceNode(
                                FlowType.of("java.io.InputStream")
                            ))
                        )
                    )
                ),
                "::"
            ),
            scope
        )));
    }

    private static ExpressionNode transformCast(ExpressionBaseNode root, BinaryExpressionNode binaryExpressionNode) {
        if (!(binaryExpressionNode.right instanceof TypeReferenceNode typeReferenceNode)) {
            LoggerFacade.error("Right side of a cast must be a type reference", root);
            return null;
        }

        return new CastExpressionNode(binaryExpressionNode.left, typeReferenceNode.type);
    }

    private static ExpressionNode transformIs(ExpressionBaseNode root, BinaryExpressionNode binaryExpressionNode) {
        if (!(binaryExpressionNode.right instanceof TypeReferenceNode typeReferenceNode)) {
            LoggerFacade.error("Right side of a instance check must be a type reference", root);
            return null;
        }

        return new IsExpressionNode(binaryExpressionNode.left, typeReferenceNode.type, binaryExpressionNode.operator.equals("is not"));
    }

    private static ExpressionNode transformVariableReference(ExpressionBaseNode root, VariableReferenceNode referenceNode, Scope scope) {
        if (scope.type() == Scope.Type.FUNCTION && scope.findLocalVariable(referenceNode.variable))
            return referenceNode;

        if (TypeRecognize.findTypeDeclaration(referenceNode.variable, scope)) {
            return new TypeReferenceNode(
                FlowType.of(referenceNode.variable)
            );
        }

        TypeDeclarationNode containingType = scope.getContainingType();
        FieldNode field = TypeRecognize.getField(referenceNode.variable, scope);

        if (field != null) {
            return new FieldReferenceNode(
                containingType != null ? FlowType.of(containingType.name) : null,
                field.initialization.declaration.name,
                new VariableReferenceNode("this"),
                field.initialization.declaration.type,
                field,
                field.modifiers.contains("static")
            );
        }

        LoggerFacade.error("Unresolved symbol: '" + referenceNode.variable + "'", root);
        return null;
    }

    private static ExpressionNode transformLambda(LambdaExpressionNode lambdaNode, Scope scope) {
        if (lambdaNode.returnType == null)
            lambdaNode.returnType = FlowType.of("Void");

        FunctionLoader.loadSignature(lambdaNode, scope, false, true);

        for (ParameterNode parameterNode : lambdaNode.parameters)
            parameterNode.type.shouldBePrimitive = false;

        FunctionLoader.loadBody(
            lambdaNode,
            lambdaNode.body.scope
        );

        final TypeDeclarationNode containingType = scope.getContainingType();
        lambdaNode.containingType = containingType;
        if (containingType != null) {
            containingType.methods.add(lambdaNode);
        }
        scope.symbols().functions().add(lambdaNode);

        return lambdaNode;
    }

    private static ExpressionNode transformFunctionCall(ExpressionBaseNode root, FunctionCallNode functionCallNode, Scope scope) {
        final FunctionDeclarationNode function;

        for (final ArgumentNode argNode : functionCallNode.arguments) {
            argNode.type = new ExpressionTraverse().traverse(argNode.value, scope);
        }

        if (functionCallNode.callerType != null) {
            final TypeDeclarationNode caller = TypeRecognize.getTypeDeclaration(functionCallNode.callerType.name, scope);

            if (caller == null) {
                LoggerFacade.error("Unresolved symbol: '" + functionCallNode.callerType + "'", root);
                return null;
            }

            final List<FunctionDeclarationNode> functions = caller.findMethodsWithName(
                scope,
                functionCallNode.name
            );

            function = functions.stream()
                .filter(method -> ParameterTraverse.compareParametersWithArguments(
                    scope,
                    method.parameters,
                    functionCallNode.arguments,
                    functionCallNode.callerType
                )).findFirst().orElse(null);
        } else {
            if (scope.findLocalVariable("this")) {
                return ExpressionTraverse.transformBinaryOperator(
                    root,
                    new BinaryExpressionNode(
                        new VariableReferenceNode("this"),
                        functionCallNode,
                        "."
                    ),
                    scope
                );
            }

            final TypeDeclarationNode containingType = scope.getContainingType();
            if (containingType != null) {
                function = ParameterTraverse.findMethodByArguments(
                    scope,
                    containingType.methods,
                    functionCallNode.name,
                    functionCallNode.arguments,
                    FlowType.of(containingType.name)
                );
            } else {
                function = ParameterTraverse.findMethodByArguments(
                    scope,
                    functionCallNode.name,
                    functionCallNode.arguments,
                    null
                );
            }
        }

        if (function == null) {
            ExpressionNode varReference = transformVariableReference(root, new VariableReferenceNode(functionCallNode.name), scope);

            return new FunctionCallNode(
                determineType(root, varReference, scope),
                varReference,
                false,
                "invoke",
                functionCallNode.arguments
            );
        }

        return functionCallNode;
    }

    private static FlowType determineType(ExpressionBaseNode root, ExpressionNode expression, Scope scope) {
        if (expression == null) {
            return null;
        }
        if (expression instanceof TypeReferenceNode typeReference) {
            if (!TypeRecognize.findTypeDeclaration(typeReference.type.name, scope)) {
                LoggerFacade.error("Type '" + typeReference.type + "' was not found", root);
                return null;
            }

            boolean isValid = true;
            for (TypeArgument typeArgument : typeReference.type.typeArguments) {
                isValid = isValid && determineType(root, typeArgument, scope) != null;
            }

            return isValid ? typeReference.type : null;
        }
        if (expression instanceof TypeArgument typeArgument) {
            if (!TypeRecognize.findTypeDeclaration(typeArgument.type.name, scope)) {
                LoggerFacade.error("Type '" + typeArgument.type + "' was not found", root);
                return null;
            }

            boolean isValid = true;
            for (TypeArgument typeSubArgument : typeArgument.type.typeArguments) {
                isValid = isValid && determineType(root, typeSubArgument, scope) != null;
            }

            return isValid ? typeArgument.type : null;
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

            boolean isValid = true;
            for (TypeArgument typeArgument : objectNode.type.typeArguments) {
                isValid = isValid && determineType(root, typeArgument, scope) != null;
            }

            if (!isValid) return null;

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
                LoggerFacade.error("Type arguments do not match type parameters, actual: '" + objectNode.type + "', expected: '" + baseType.typeParameters + "'", root);
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
                if (argNode.type == null)
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

                if (function != null)
                    if (function.modifiers.contains("static") && !(functionCall.caller instanceof TypeReferenceNode)) {
                        LoggerFacade.error("Cannot access static members via an instance", root);
                    } else if (!function.modifiers.contains("static") && functionCall.caller instanceof TypeReferenceNode) {
                        LoggerFacade.error("Cannot access non-static members in a static context", root);
                    }
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
                    FlowType.of(containingType.name),
                    functionCall.callerType,
                    scope
                ))
            ) {
                LoggerFacade.error("Cannot access '" + function.name + "', it is " + modifier + " in '" + functionCall.callerType + "'", root);
                return null;
            }

            return !functionCall.isSafeCall ? function.returnType : FlowType.nullable(function.returnType.name);
        }
        if (expression instanceof LambdaExpressionNode lambdaNode) {
            return getLambdaType(lambdaNode.parameters, lambdaNode.returnType);
        }
        if (expression instanceof MethodReferenceNode methodReferenceNode) {
            return getLambdaType(methodReferenceNode.method.parameters, methodReferenceNode.method.returnType);
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
                        FlowType.of(containingType.name),
                        FlowType.of(holder.name),
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
        if (expression instanceof CastExpressionNode castExpressionNode) {
            castExpressionNode.castType.shouldBePrimitive = false;
            return castExpressionNode.castType;
        }
        if (expression instanceof IsExpressionNode) {
            return FlowType.primitive("Bool");
        }
        if (expression instanceof ConnectionNode connectionNode) {
            return FlowType.of(
                "flow.networking.Socket",
                List.of(
                    new TypeArgument(
                        connectionNode.protocolType.type
                    )
                )
            );
        }
        if (expression instanceof LiteralNode literalNode) {
            return FlowType.primitive(literalNode.getClassName());
        }
        if (expression instanceof NullLiteral) {
            return FlowType.nullable("null");
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

    private static FlowType getLambdaType(List<ParameterNode> parameters, FlowType returnType) {
        final boolean hasReturnValue = !returnType.name.equals("Void");

        List<TypeArgument> typeArguments = new ArrayList<>(parameters.stream()
            .map((parameterNode) -> new TypeArgument(
                parameterNode.type
            )).toList());

        if (hasReturnValue)
            typeArguments.add(new TypeArgument(returnType));

        return FlowType.of(
            getLambdaInterfaceName(parameters.size(), hasReturnValue),
            typeArguments
        );
    }

    public static String getLambdaInterfaceName(int argumentCount, boolean hasReturnType) {
        if (hasReturnType) {
            return "Function" + argumentCount;
        } else {
            if (argumentCount == 0) return "Procedure";

            return "Consumer" + argumentCount;
        }
    }

    public enum UnaryOperatorType {
        MUTATING,
        FUNCTION
    }
}
