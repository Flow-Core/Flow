package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.classes.*;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.visitors.ExpressionTraverse;
import semantic_analysis.visitors.ParameterTraverse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassLoader implements ASTVisitor<Scope> {
    @Override
    public void visit(final ASTNode node, final Scope scope) {
        if (node instanceof ClassDeclarationNode classDeclaration) {
            handleClass(classDeclaration, scope);
        } else if (node instanceof InterfaceNode interfaceDeclaration) {
            handleInterface(interfaceDeclaration, scope);
        }
    }

    private void handleClass(final ClassDeclarationNode classDeclaration, final Scope scope) {
        ModifierLoader.load(classDeclaration, classDeclaration.modifiers, ModifierLoader.ModifierType.CLASS);

        validateBaseClass(classDeclaration, scope);
        validateInterfaces(classDeclaration.implementedInterfaces, scope);

        loadTypeParameters(classDeclaration, scope);

        checkAbstractMethods(classDeclaration);

        if (!classDeclaration.modifiers.contains("abstract")) {
            checkIfAllOverridden(classDeclaration, scope);
        }

        loadConstructors(classDeclaration, scope);
    }

    private void loadTypeParameters(final TypeDeclarationNode typeDeclarationNode, final Scope scope) {
        for (final TypeParameterNode typeParameterNode : typeDeclarationNode.typeParameters) {
            final TypeDeclarationNode bound = scope.getTypeDeclaration(typeParameterNode.bound.name);
            if (bound == null) {
                LoggerFacade.error("Unresolved symbol: '" + typeParameterNode.bound.name + "'", typeDeclarationNode);
                return;
            }

            typeParameterNode.updateBound(bound);
            scope.symbols().typeParameters().add(typeParameterNode);
        }
    }

    private void loadConstructors(final ClassDeclarationNode classDeclaration, final Scope scope) {
        for (final ConstructorNode constructorNode : classDeclaration.constructors) {
            ModifierLoader.load(constructorNode, List.of(constructorNode.accessModifier), ModifierLoader.ModifierType.CONSTRUCTOR);

            if (
                classDeclaration.constructors.stream()
                    .filter(
                        constructor -> ParameterTraverse.compareParameterTypes(
                            scope,
                            constructor.parameters,
                            constructorNode.parameters.stream()
                                .map(parameter -> parameter.type).toList()
                        )
                    ).toList().size() > 1
            ) {
                LoggerFacade.error("Cannot have more than one constructor with the same signature", classDeclaration);
                return;
            }
        }
    }

    private void checkIfAllOverridden(final ClassDeclarationNode classDeclaration, final Scope scope) {
        final List<FunctionDeclarationNode> abstractFunctions = getFunctionsByModifier("abstract", classDeclaration, scope);
        final List<FunctionDeclarationNode> overriddenFunctions = getFunctionsByModifier("override", classDeclaration, scope);

        for (final FunctionDeclarationNode abstractFunction : abstractFunctions) {
            final FunctionDeclarationNode method = overriddenFunctions.stream()
                .filter(function -> function.name.equals(abstractFunction.name))
                .filter(function -> ParameterTraverse.compareParameterTypes(scope, abstractFunction.parameters, function.parameters.stream().map(parameterNode -> parameterNode.type).toList()))
                .findFirst().orElse(null);
            if (method == null) {
                LoggerFacade.error("Class '" + classDeclaration.name + "' is not abstract and does not implement abstract base class member '" + abstractFunction.name + "'", classDeclaration);
                return;
            } else if ((method.returnType.isNullable != abstractFunction.returnType.isNullable) ||
                    !scope.isSameType(
                        method.returnType,
                        abstractFunction.returnType
                    )
            ) {
                LoggerFacade.error("Return type of function '" + abstractFunction.name + "' is not a subtype of the overridden member, expected a subtype of: '" + abstractFunction.returnType + "' but found '" + method.returnType + "'", abstractFunction);
                return;
            }
        }

        for (final FunctionDeclarationNode overriddenFunction : overriddenFunctions) {
            if (
                ParameterTraverse.findMethodWithParameters(
                    scope,
                    classDeclaration.getAllSuperFunctions(scope),
                    overriddenFunction.name,
                    overriddenFunction.parameters.stream()
                        .map(parameter -> parameter.type).toList()
                ) == null
            ) {
                LoggerFacade.error("'" + overriddenFunction.name + "' overrides nothing", overriddenFunction);
                return;
            }
        }
    }

    private void validateBaseClass(ClassDeclarationNode classDeclaration, Scope scope) {
        if (!classDeclaration.baseClasses.isEmpty()) {
            if (classDeclaration.baseClasses.size() > 1) {
                LoggerFacade.error("Class '" + classDeclaration.name + "' cannot extend more than one class", classDeclaration);
                return;
            }

            final BaseClassNode baseClassNode = classDeclaration.baseClasses.get(0);
            final ClassDeclarationNode baseClass = scope.getClass(baseClassNode.name);
            if (baseClass == null) {
                LoggerFacade.error("Base class '" + baseClassNode.name + "' for class '" + classDeclaration.name + "' was not found", classDeclaration);
                return;
            }

            if (!baseClass.modifiers.contains("open") && !baseClass.modifiers.contains("abstract")) {
                LoggerFacade.error("'" + baseClassNode.name + "' is final, so it cannot be extended", classDeclaration);
            }

            if (classDeclaration.name.equals(baseClassNode.name)) {
                throw LoggerFacade.getLogger().panic(
                    "Class cannot extend itself: " + classDeclaration.name,
                    ASTMetaDataStore.getInstance().getLine(classDeclaration),
                    ASTMetaDataStore.getInstance().getFile(classDeclaration)
                );
            }

            checkCircularInheritance(classDeclaration.name, baseClass, new HashSet<>(), scope);

            new ExpressionTraverse().traverse(
                new ExpressionBaseNode(
                    baseClassNode,
                    ASTMetaDataStore.getInstance().getLine(baseClassNode),
                    ASTMetaDataStore.getInstance().getFile(baseClassNode)
                ),
                scope
            );

            scope.parent().symbols().bindingContext().put(baseClassNode, scope.getFQName(baseClass));
        }
    }

    private void checkCircularInheritance(
        String originalClass,
        ClassDeclarationNode currentClass,
        Set<String> visited,
        Scope scope
    ) {
        if (visited.contains(currentClass.name)) {
            throw LoggerFacade.getLogger().panic(
                "Circular inheritance detected: '" + originalClass + "' -> '" + currentClass.name + "'",
                ASTMetaDataStore.getInstance().getLine(currentClass),
                ASTMetaDataStore.getInstance().getFile(currentClass)
            );
        }

        visited.add(currentClass.name);

        for (BaseClassNode base : currentClass.baseClasses) {
            ClassDeclarationNode nextBase = scope.getClass(base.name);
            if (nextBase != null) {
                checkCircularInheritance(originalClass, nextBase, new HashSet<>(visited), scope);
            }
        }
    }

    private void validateInterfaces(List<BaseInterfaceNode> interfaces, Scope scope) {
        for (final BaseInterfaceNode interfaceNode : interfaces) {
            final InterfaceNode baseInterface = scope.getInterface(interfaceNode.name);
            if (baseInterface == null) {
                LoggerFacade.error("Interface '" + interfaceNode.name + "' was not found", interfaceNode);
                return;
            }

            scope.symbols().bindingContext().put(interfaceNode, scope.getFQName(baseInterface));
        }
    }

    private void checkCircularInterfaceInheritance(
        String originalInterface,
        InterfaceNode currentInterface,
        Set<String> visited,
        Scope scope
    ) {
        if (visited.contains(currentInterface.name)) {
            LoggerFacade.error("Circular inheritance detected: '" + originalInterface + "' -> '" + currentInterface.name + "'", currentInterface);
            return;
        }

        visited.add(currentInterface.name);

        for (BaseInterfaceNode base : currentInterface.implementedInterfaces) {
            InterfaceNode nextInterface = scope.getInterface(base.name);
            if (nextInterface != null) {
                checkCircularInterfaceInheritance(currentInterface.name, nextInterface, new HashSet<>(visited), scope);
            }
        }
    }

    private void validateInterfaces(InterfaceNode interfaceNode, Scope scope) {
        for (final BaseInterfaceNode currentInterface : interfaceNode.implementedInterfaces) {
            final InterfaceNode baseInterface = scope.getInterface(currentInterface.name);
            if (baseInterface == null) {
                LoggerFacade.error("Interface '" + currentInterface.name + "' was not found", currentInterface);
                return;
            }

            if (currentInterface.name.equals(interfaceNode.name)) {
                LoggerFacade.error("Interface cannot extend itself: " + interfaceNode.name, currentInterface);
                return;
            }

            checkCircularInterfaceInheritance(interfaceNode.name, interfaceNode, new HashSet<>(), scope);

            scope.symbols().bindingContext().put(currentInterface, scope.getFQName(baseInterface));
        }
    }

    private void checkAbstractMethods(ClassDeclarationNode classDeclaration) {
        for (FunctionDeclarationNode functionDeclaration : classDeclaration.methods) {
            if (functionDeclaration.modifiers.contains("abstract")) {
                if (!classDeclaration.modifiers.contains("abstract")) {
                    LoggerFacade.error("Abstract function '" + functionDeclaration.name + "' in non-abstract class '" + classDeclaration.name + "'", functionDeclaration);
                    return;
                }
                if (functionDeclaration.body.blockNode != null) {
                    LoggerFacade.error("Abstract function '" + functionDeclaration.name + "' cannot have a block", functionDeclaration);
                    return;
                }
            } else if (functionDeclaration.body.blockNode == null) {
                LoggerFacade.error("Function '" + functionDeclaration.name + "' without a body must be abstract", functionDeclaration);
                return;
            }
        }
    }

    private void handleInterface(final InterfaceNode interfaceDeclaration, final Scope scope) {
        ModifierLoader.load(interfaceDeclaration.modifiers, ModifierLoader.ModifierType.INTERFACE);

        loadTypeParameters(interfaceDeclaration, scope);

        validateInterfaces(interfaceDeclaration, scope);
    }

    private List<FunctionDeclarationNode> getFunctionsByModifier(
        final String modifier,
        final TypeDeclarationNode typeDeclarationNode,
        final Scope scope
    ) {
        final List<FunctionDeclarationNode> foundFunctions = new ArrayList<>();

        for (final FunctionDeclarationNode functionDeclarationNode : typeDeclarationNode.methods) {
            if (functionDeclarationNode.modifiers.contains(modifier)) {
                foundFunctions.add(functionDeclarationNode);
            }
        }

        if (typeDeclarationNode instanceof ClassDeclarationNode classDeclarationNode) {
            if (!classDeclarationNode.baseClasses.isEmpty()) {
                final String baseClassName = classDeclarationNode.baseClasses.get(0).name;
                
                final ClassDeclarationNode baseClass = scope.getClass(baseClassName);
                if (baseClass == null) {
                    LoggerFacade.error("Base class '" + baseClassName + "' for class '" + classDeclarationNode.name + "' was not found", classDeclarationNode);
                    return new ArrayList<>();
                }
                
                foundFunctions.addAll(getFunctionsByModifier(modifier, baseClass, scope));
            }
        }

        if (modifier.equals("abstract")) {
            for (final BaseInterfaceNode baseInterfaceNode : typeDeclarationNode.implementedInterfaces) {
                final InterfaceNode interfaceNode = scope.getInterface(baseInterfaceNode.name);
                if (interfaceNode == null) {
                    LoggerFacade.error("Interface '" + baseInterfaceNode.name + "' was not found", baseInterfaceNode);
                    return new ArrayList<>();
                }

                foundFunctions.addAll(interfaceNode.methods);
                foundFunctions.addAll(getFunctionsByModifier(modifier, interfaceNode, scope));
            }
        }

        return foundFunctions;
    }
}
