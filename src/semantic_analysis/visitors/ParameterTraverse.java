package semantic_analysis.visitors;

import logger.LoggerFacade;
import parser.nodes.FlowType;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeArgument;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterTraverse {
    public static FunctionDeclarationNode findMethodWithParameters(
        Scope scope,
        List<FunctionDeclarationNode> methods,
        String name,
        List<FlowType> parameterTypes
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParameterTypes(scope, method.parameters, parameterTypes))
            .findFirst().orElse(null);
    }

    public static FunctionDeclarationNode findMethodByArguments(
        Scope scope,
        List<FunctionDeclarationNode> methods,
        String name,
        List<ArgumentNode> arguments,
        FlowType callerType
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParametersWithArguments(scope, method.parameters, arguments, callerType))
            .findFirst().orElse(null);
    }

    public static ConstructorNode findConstructor(
        Scope scope,
        List<ConstructorNode> constructors,
        List<ArgumentNode> arguments,
        FlowType callerType
    ) {
        return constructors.stream()
            .filter(method -> compareParametersWithArguments(scope, method.parameters, arguments, callerType))
            .findFirst().orElse(null);
    }

    public static FunctionDeclarationNode findMethodByArguments(
        Scope scope,
        String name,
        List<ArgumentNode> arguments,
        FlowType callerType
    ) {
        FunctionDeclarationNode declaration = null;

        while (declaration == null && scope != null && scope.parent() != null) {
            declaration = findMethodByArguments(scope, scope.symbols().functions(), name, arguments, callerType);

            scope = scope.parent();
        }

        return declaration;
    }

    public static FunctionDeclarationNode findMethodWithParameters(
        Scope scope,
        String name,
        List<FlowType> parameterTypes
    ) {
        FunctionDeclarationNode declaration = null;

        while (declaration == null && scope != null && scope.parent() != null) {
            declaration = findMethodWithParameters(scope, scope.symbols().functions(), name, parameterTypes);

            scope = scope.parent();
        }

        return declaration;
    }

    public static boolean compareTypeParameters(
        Scope scope,
        List<TypeParameterNode> typeParameters,
        List<TypeArgument> typeArguments
    ) {
        if (typeArguments.size() != typeParameters.size()) return false;

        for (int i = 0; i < typeParameters.size(); i++) {
            if (!TypeRecognize.isSameType(
                typeArguments.get(i).type,
                typeParameters.get(i).bound,
                scope
            ))
                return false;
        }

        return true;
    }

    public static boolean compareParameterTypes(
        Scope scope,
        List<ParameterNode> parameters,
        List<FlowType> parameterTypes
    ) {
        if (parameterTypes.size() != parameters.size()) return false;

        for (int i = 0; i < parameters.size(); i++) {
            if (!TypeRecognize.isSameType(
                parameterTypes.get(i),
                parameters.get(i).type,
                scope
            ))
                return false;
        }

        return true;
    }

    public static boolean compareParametersWithArguments(
        Scope scope,
        List<ParameterNode> parameters,
        List<ArgumentNode> arguments,
        FlowType callerType
    ) {
        Map<String, FlowType> typeArgumentMapping = new HashMap<>();

        if (callerType != null) {
            TypeDeclarationNode typeDeclarationNode = TypeRecognize.getTypeDeclaration(callerType.name, scope);

            if (typeDeclarationNode == null || typeDeclarationNode.typeParameters.size() != callerType.typeArguments.size()) {
                return false;
            }

            for (int i = 0; i < typeDeclarationNode.typeParameters.size(); i++) {
                typeArgumentMapping.put(typeDeclarationNode.typeParameters.get(i).name, callerType.typeArguments.get(i).type);
            }
        }

        if (parameters.size() < arguments.size()) return false;

        boolean foundNamed = false;
        List<ParameterNode> passedArgument = new ArrayList<>();

        for (int i = 0; i < arguments.size(); i++) {
            final ArgumentNode argumentNode = arguments.get(i);
            final ParameterNode parameterNode;

            if (argumentNode.name != null) {
                foundNamed = true;

                parameterNode = parameters.stream()
                    .filter(parameter -> parameter.name.equals(argumentNode.name))
                    .findFirst().orElse(null);

                if (parameterNode == null) {
                    LoggerFacade.error("Unresolved symbol: '" + argumentNode.name + "'", argumentNode);
                    return false;
                }
            } else if (foundNamed) {
                LoggerFacade.error("Unnamed arguments cannot follow named arguments", argumentNode);
                return false;
            } else {
                parameterNode = parameters.get(i);
            }

            passedArgument.add(parameterNode);

            FlowType paramType = parameterNode.type;
            if (typeArgumentMapping.containsKey(paramType.name)) {
                paramType = typeArgumentMapping.get(paramType.name);
            }

            if (!TypeRecognize.isSameType(
                argumentNode.type,
                paramType,
                scope
            ))
                return false;
        }

        for (ParameterNode parameter : parameters) {
            if (parameter.defaultValue == null && !passedArgument.contains(parameter))
                return false;
        }

        return true;
    }
}
