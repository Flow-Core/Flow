package semantic_analysis.scopes;

import logger.LoggerFacade;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.files.PackageWrapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TypeRecognize {
    private static Map<String, PackageWrapper> packages;

    public static void init(Map<String, PackageWrapper> packageWrappers) {
        packages = packageWrappers;
    }

    public static ClassDeclarationNode getClass(String symbol, Scope scope) {
        if (symbol.contains(".")) {
            final PackageWrapper containingPackage = packages.get(extractPackageName(symbol));

            if (containingPackage == null) {
                return null;
            }

            return containingPackage.scope().getClass(trimPackageName(symbol));
        }

        return scope.getClass(symbol);
    }

    public static InterfaceNode getInterface(String symbol, Scope scope) {
        if (symbol.contains(".")) {
            final PackageWrapper containingPackage = packages.get(extractPackageName(symbol));

            if (containingPackage == null) {
                return null;
            }

            return containingPackage.scope().getInterface(trimPackageName(symbol));
        }

        return scope.getInterface(symbol);
    }

    public static TypeParameterNode getTypeParameter(String symbol, Scope scope) {
        return scope.getTypeParameter(symbol);
    }

    public static TypeDeclarationNode getTypeDeclaration(String symbol, Scope scope) {
        if (symbol.contains(".")) {
            final PackageWrapper containingPackage = packages.get(extractPackageName(symbol));

            if (containingPackage == null) {
                return null;
            }

            return containingPackage.scope().getTypeDeclaration(trimPackageName(symbol));
        }

        return scope.getTypeDeclaration(symbol);
    }

    public static FunctionDeclarationNode getFunction(String symbol, Scope scope) {
        if (symbol.contains(".")) {
            final PackageWrapper containingPackage = packages.get(extractPackageName(symbol));
            return containingPackage.scope().getFunction(trimPackageName(symbol));
        }

        return scope.getFunction(symbol);
    }

    public static FieldNode getField(String symbol, Scope scope) {
        if (symbol.contains(".")) {
            final PackageWrapper containingPackage = packages.get(extractPackageName(symbol));
            return containingPackage.scope().getField(trimPackageName(symbol));
        }

        return scope.getField(symbol);
    }

    public static boolean findClass(String symbol, Scope scope) {
        return getClass(symbol, scope) != null;
    }

    public static boolean findInterface(String symbol, Scope scope) {
        return getInterface(symbol, scope) != null;
    }

    public static boolean findTypeParameter(String symbol, Scope scope) {
        return getTypeParameter(symbol, scope) != null;
    }

    public static boolean findTypeDeclaration(String symbol, Scope scope) {
        return findClass(symbol, scope) || findInterface(symbol, scope) || findTypeParameter(symbol, scope);
    }

    public static boolean findFunction(String symbol, Scope scope) {
        return getFunction(symbol, scope) != null;
    }

    public static boolean findField(String symbol, Scope scope) {
        return getField(symbol, scope) != null;
    }

    public static boolean isSameType(FlowType type, FlowType superType, Scope scope) {
        if (type == null || superType == null) {
            return false;
        }

        if (!superType.isNullable && type.isNullable)
            return false;

        if (type.name.equals(superType.name)) {
            return true;
        }

        if (superType.name.equals("java.lang.Object")) {
            return true;
        }

        final ClassDeclarationNode classDeclarationNode = getClass(type.name, scope);
        if (classDeclarationNode != null) {
            if (!classDeclarationNode.baseClasses.isEmpty() && classDeclarationNode.baseClasses.get(0).type.name.equals(superType.name)) {
                return true;
            }
            if (!classDeclarationNode.baseClasses.isEmpty() &&
                isSameType(
                    new FlowType(
                        classDeclarationNode.baseClasses.get(0).type.name,
                        type.isNullable,
                        type.isPrimitive
                    ),
                    superType,
                    scope
                )
            ) {
                return true;
            }
        }

        final TypeDeclarationNode typeDeclarationNode = getTypeDeclaration(type.name, scope);

        if (typeDeclarationNode instanceof TypeParameterNode) {
            return isSameType(
                getEffectiveType(type, scope, new HashSet<>()),
                superType,
                scope
            );
        }

        if (typeDeclarationNode != null) {
            final TypeDeclarationNode superTypeDeclaration = getTypeDeclaration(superType.name, scope);

            if (superTypeDeclaration instanceof TypeParameterNode) {
                return isSameType(
                    type,
                    getEffectiveType(superType, scope, new HashSet<>()),
                    scope
                );
            }

            if (typeDeclarationNode.equals(superTypeDeclaration)) {
                return true;
            }

            for (final BaseInterfaceNode baseInterfaceNode : typeDeclarationNode.implementedInterfaces) {
                if (baseInterfaceNode.type.name.equals(superType.name) ||
                    isSameType(
                        new FlowType(
                            baseInterfaceNode.type.name,
                            type.isNullable,
                            type.isPrimitive
                        ),
                        superType,
                        scope
                    )
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    public static FlowType getEffectiveType(FlowType type, Scope scope, Set<String> visited) {
        if (!visited.add(type.name)) {
            throw LoggerFacade.getLogger().panic("Circular parameter type found for: '" + type + "'");
        }

        final TypeDeclarationNode param = getTypeDeclaration(type.name, scope);

        if (param instanceof TypeParameterNode typeParameterNode) {
            return getEffectiveType(typeParameterNode.bound, scope, visited);
        }

        return type;
    }

    public static String trimPackageName(String name) {
        int lastDotIndex = name.lastIndexOf(".");
        return (lastDotIndex != -1) ? name.substring(lastDotIndex + 1) : name;
    }

    private static String extractPackageName(String name) {
        int lastSlashIndex = name.lastIndexOf('.');
        return (lastSlashIndex != -1) ? name.substring(0, lastSlashIndex) : "";
    }
}
