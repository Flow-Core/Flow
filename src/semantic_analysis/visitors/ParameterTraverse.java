package semantic_analysis.visitors;

import logger.LoggerFacade;
import parser.nodes.FlowType;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;

import java.util.ArrayList;
import java.util.List;

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
        List<ArgumentNode> arguments
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParametersWithArguments(scope, method.parameters, arguments))
            .findFirst().orElse(null);
    }

    public static FunctionDeclarationNode findMethodByArguments(
        Scope scope,
        String name,
        List<ArgumentNode> arguments
    ) {
        FunctionDeclarationNode declaration = null;

        while (declaration == null && scope != null && scope.parent() != null) {
            declaration = findMethodByArguments(scope, scope.symbols().functions(), name, arguments);

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
        List<ArgumentNode> arguments
    ) {
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

            if (!TypeRecognize.isSameType(
                argumentNode.type,
                parameterNode.type,
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
