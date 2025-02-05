package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.exceptions.SA_RedefinitionException;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.visitors.ExpressionTraverse.TypeWrapper;

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
        boolean isPublic = !classDeclaration.modifiers.contains("private") && !classDeclaration.modifiers.contains("protected");

        if (packageWrapper.scope().findSymbol(classDeclaration.name)) {
            throw new SA_RedefinitionException(classDeclaration.name);
        }

        if (isPublic) {
            packageWrapper.scope().symbols().classes().add(classDeclaration);
            packageWrapper.scope().symbols().bindingContext().put(classDeclaration, joinPath(packageWrapper.path(), classDeclaration.name));
        } else {
            fileLevel.classes().add(classDeclaration);
        }
    }

    private static void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = !interfaceDeclaration.modifiers.contains("private") && !interfaceDeclaration.modifiers.contains("protected");

        if (packageWrapper.scope().findSymbol(interfaceDeclaration.name)) {
            throw new SA_RedefinitionException(interfaceDeclaration.name);
        }

        if (isPublic) {
            packageWrapper.scope().symbols().interfaces().add(interfaceDeclaration);
            packageWrapper.scope().symbols().bindingContext().put(interfaceDeclaration, joinPath(packageWrapper.path(), interfaceDeclaration.name));
        } else {
            fileLevel.interfaces().add(interfaceDeclaration);
        }
    }

    private static void handleFunction(final FunctionDeclarationNode functionDeclarationNode, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = !functionDeclarationNode.modifiers.contains("private") && !functionDeclarationNode.modifiers.contains("protected");

        if (isPublic) {
            packageWrapper.scope().symbols().functions().add(functionDeclarationNode);
            packageWrapper.scope().symbols().bindingContext().put(functionDeclarationNode, joinPath(packageWrapper.path(), functionDeclarationNode.name));
        } else {
            fileLevel.functions().add(functionDeclarationNode);
        }
    }

    private static void handleField(final FieldNode fieldNode, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = !fieldNode.modifiers.contains("private") && !fieldNode.modifiers.contains("protected");

        final String name = fieldNode.initialization.declaration.name;
        if (packageWrapper.scope().findSymbol(name)) {
            throw new SA_RedefinitionException(name);
        }

        if (isPublic) {
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
}