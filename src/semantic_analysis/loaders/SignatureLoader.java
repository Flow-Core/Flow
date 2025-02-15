package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.exceptions.SA_RedefinitionException;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.visitors.ExpressionTraverse.TypeWrapper;

import java.util.ArrayList;
import java.util.List;

import static semantic_analysis.scopes.SymbolTable.joinPath;

public class SignatureLoader {
    public static void load(final List<ASTNode> nodes, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        for (final ASTNode node : nodes) {
            if (node instanceof ClassDeclarationNode classDeclarationNode) {
                handleClass(classDeclarationNode, fileLevel, packageWrapper);
            } else if (node instanceof InterfaceNode interfaceNode) {
                handleInterface(interfaceNode, fileLevel, packageWrapper);
            } else if (node instanceof FunctionDeclarationNode functionDeclarationNode) {
                handleFunction(functionDeclarationNode, fileLevel, packageWrapper);
            } else if (node instanceof FieldNode fieldNode) {
                handleField(fieldNode, fileLevel, packageWrapper);
            }
        }
    }

    private static void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = ModifierLoader.isPublic(classDeclaration.modifiers);

        if (packageWrapper.scope().findSymbol(classDeclaration.name)) {
            throw new SA_RedefinitionException(classDeclaration.name);
        }

        if (!classDeclaration.modifiers.contains("abstract")) {
            addPrimaryConstructor(classDeclaration);
        }

        if (isPublic) {
            if (ModifierLoader.isDefaultPublic(classDeclaration.modifiers)) {
                classDeclaration.modifiers.add("public");
            }

            packageWrapper.scope().symbols().classes().add(classDeclaration);
            packageWrapper.scope().symbols().bindingContext().put(classDeclaration, joinPath(packageWrapper.path(), classDeclaration.name));
        } else {
            fileLevel.classes().add(classDeclaration);
        }
    }

    private static void addPrimaryConstructor(ClassDeclarationNode classDeclaration) {
        List<ParameterNode> primaryParameters = new ArrayList<>();
        List<ASTNode> assignments = new ArrayList<>();

        for (FieldNode field : classDeclaration.primaryConstructor) {
            primaryParameters.add(
                new ParameterNode(
                    field.initialization.declaration.type,
                    field.initialization.declaration.isNullable,
                    field.initialization.declaration.name,
                    null
                )
            );

            classDeclaration.fields.add(0, field);
            assignments.add(
                new VariableAssignmentNode(
                    new ExpressionBaseNode(new VariableReferenceNode(field.initialization.declaration.name)),
                    "=",
                    new ExpressionBaseNode(
                        new BinaryExpressionNode(
                            new VariableReferenceNode("this"),
                            new VariableReferenceNode(field.initialization.declaration.name),
                            "."
                        )
                    )
                )
            );
        }

        ConstructorNode primaryConstructor = new ConstructorNode(
            "public",
            primaryParameters,
            new BlockNode(assignments)
        );

        classDeclaration.constructors.add(primaryConstructor);
    }

    private static void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = ModifierLoader.isPublic(interfaceDeclaration.modifiers);

        if (packageWrapper.scope().findSymbol(interfaceDeclaration.name)) {
            throw new SA_RedefinitionException(interfaceDeclaration.name);
        }

        if (isPublic) {
            if (ModifierLoader.isDefaultPublic(interfaceDeclaration.modifiers)) {
                interfaceDeclaration.modifiers.add("public");
            }

            packageWrapper.scope().symbols().interfaces().add(interfaceDeclaration);
            packageWrapper.scope().symbols().bindingContext().put(interfaceDeclaration, joinPath(packageWrapper.path(), interfaceDeclaration.name));
        } else {
            fileLevel.interfaces().add(interfaceDeclaration);
        }
    }

    private static void handleFunction(final FunctionDeclarationNode functionDeclarationNode, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = ModifierLoader.isPublic(functionDeclarationNode.modifiers);

        if (isPublic) {
            if (ModifierLoader.isDefaultPublic(functionDeclarationNode.modifiers)) {
                functionDeclarationNode.modifiers.add("public");
            }

            packageWrapper.scope().symbols().functions().add(functionDeclarationNode);
            packageWrapper.scope().symbols().bindingContext().put(functionDeclarationNode, joinPath(packageWrapper.path(), functionDeclarationNode.name));
        } else {
            fileLevel.functions().add(functionDeclarationNode);
        }
    }

    private static void handleField(final FieldNode fieldNode, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = ModifierLoader.isPublic(fieldNode.modifiers);

        if (fieldNode.initialization == null) {
            return;
        }

        final String name = fieldNode.initialization.declaration.name;
        if (packageWrapper.scope().findSymbol(name)) {
            throw new SA_RedefinitionException(name);
        }

        if (isPublic) {
            if (ModifierLoader.isDefaultPublic(fieldNode.modifiers)) {
                fieldNode.modifiers.add("public");
            }

            packageWrapper.scope().symbols().fields().add(fieldNode);
            packageWrapper.scope().symbols().bindingContext().put(fieldNode, joinPath(packageWrapper.path(), fieldNode.initialization.declaration.name));
        } else {
            fileLevel.fields().add(fieldNode);
        }
    }

    public static FunctionDeclarationNode findMethodWithParameters(
        Scope scope,
        List<FunctionDeclarationNode> methods,
        String name,
        List<TypeWrapper> parameterTypes,
        boolean ignoreThis
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParameterTypes(scope, method.parameters, parameterTypes, ignoreThis))
            .findFirst().orElse(null);
    }

    public static FunctionDeclarationNode findMethodWithParameters(
        SymbolTable symbols,
        List<FunctionDeclarationNode> methods,
        String name,
        List<TypeWrapper> parameterTypes,
        boolean ignoreThis
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParameterTypes(symbols, method.parameters, parameterTypes, ignoreThis))
            .findFirst().orElse(null);
    }

    public static FunctionDeclarationNode findMethodWithParameters(
        Scope scope,
        List<FunctionDeclarationNode> methods,
        String name,
        List<TypeWrapper> parameterTypes
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParameterTypes(scope, method.parameters, parameterTypes, false))
            .findFirst().orElse(null);
    }

    public static FunctionDeclarationNode findMethodWithParameters(
        SymbolTable symbols,
        List<FunctionDeclarationNode> methods,
        String name,
        List<TypeWrapper> parameterTypes
    ) {
        return methods.stream()
            .filter(method -> method.name.equals(name))
            .filter(method -> compareParameterTypes(symbols, method.parameters, parameterTypes, false))
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
            .filter(method -> compareParameterTypes(scope, method.parameters, arguments))
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
        List<TypeWrapper> parameterTypes
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
        List<TypeWrapper> parameterTypes,
        boolean ignoreThis
    ) {
        if (parameterTypes.size() != parameters.size()) return false;

        for (int i = ignoreThis ? 1 : 0; i < parameters.size(); i++) {
            if (!scope.isSameType(
                parameterTypes.get(i),
                new TypeWrapper(
                    parameters.get(i).type,
                    false,
                    parameters.get(i).isNullable
                )
            ))
                return false;
        }

        return true;
    }

    public static boolean compareParameterTypes(
        SymbolTable symbols,
        List<ParameterNode> parameters,
        List<TypeWrapper> parameterTypes,
        boolean ignoreThis
    ) {
        if (parameterTypes.size() != parameters.size()) return false;

        for (int i = ignoreThis ? 1 : 0; i < parameters.size(); i++) {
            if (!symbols.isSameType(
                parameterTypes.get(i),
                new TypeWrapper(
                    parameters.get(i).type,
                    false,
                    parameters.get(i).isNullable
                )
            ))
                return false;
        }

        return true;
    }

    public static boolean compareParameterTypes(
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

                if (parameterNode == null)
                    throw new SA_UnresolvedSymbolException(argumentNode.name);
            } else if (foundNamed)
                throw new SA_SemanticError("Unnamed arguments cannot follow named arguments"); // Log
            else {
                parameterNode = parameters.get(i);
            }

            passedArgument.add(parameterNode);

            if (!scope.isSameType(
                argumentNode.type,
                new TypeWrapper(
                    parameterNode.type,
                    false,
                    parameterNode.isNullable
                )
            ))
                return false;
        }

        for (ParameterNode parameter : parameters) {
            if (parameter.defaultValue == null && !passedArgument.contains(parameter))
                return false;
        }

        return true;
    }

    public static boolean compareParameterTypesWithoutThis(
        Scope scope,
        List<ParameterNode> parameters,
        List<ArgumentNode> arguments
    ) {
        if (parameters.size() + 1 < arguments.size()) return false;

        boolean foundNamed = false;

        List<ParameterNode> passedArgument = new ArrayList<>();

        for (int i = 1; i < arguments.size(); i++) {
            final ArgumentNode argumentNode = arguments.get(i);
            final ParameterNode parameterNode;

            if (argumentNode.name != null) {
                foundNamed = true;

                parameterNode = parameters.stream()
                    .filter(parameter -> parameter.name.equals(argumentNode.name))
                    .findFirst().orElse(null);

                if (parameterNode == null)
                    throw new SA_UnresolvedSymbolException(argumentNode.name);
            } else if (foundNamed)
                throw new SA_SemanticError("Unnamed arguments cannot follow named arguments"); // Log
            else {
                parameterNode = parameters.get(i);
            }

            passedArgument.add(parameterNode);

            if (!scope.isSameType(
                argumentNode.type,
                new TypeWrapper(
                    parameterNode.type,
                    false,
                    parameterNode.isNullable
                )
            ))
                return false;
        }

        for (int i = 1; i < parameters.size(); i++) {
            ParameterNode parameter = parameters.get(i);
            if (parameter.defaultValue == null && !passedArgument.contains(parameter))
                return false;
        }

        return true;
    }
}