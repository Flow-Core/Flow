package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.visitors.ExpressionTraverse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static semantic_analysis.loaders.SignatureLoader.compareParameterTypes;
import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;

public class ClassLoader implements ASTVisitor<SymbolTable> {
    private final SymbolTable packageLevel;

    public ClassLoader(SymbolTable packageLevel) {
        this.packageLevel = packageLevel;
    }

    @Override
    public void visit(final ASTNode node, final SymbolTable data) {
        if (node instanceof ClassDeclarationNode classDeclaration) {
            handleClass(classDeclaration, data);
        } else if (node instanceof InterfaceNode interfaceDeclaration) {
            handleInterface(interfaceDeclaration, data);
        }
    }

    private void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable data) {
        ModifierLoader.load(classDeclaration, classDeclaration.modifiers, ModifierLoader.ModifierType.CLASS);

        validateBaseClass(classDeclaration, data);
        validateInterfaces(classDeclaration.implementedInterfaces, data);

        addThisParameterToInstanceMethods(classDeclaration);

        if (!classDeclaration.modifiers.contains("abstract")) {
            checkIfAllOverridden(classDeclaration, data);
        }

        loadConstructors(classDeclaration);

        data.classes().add(classDeclaration);
    }

    private void loadConstructors(final ClassDeclarationNode classDeclaration) {
        for (final ConstructorNode constructorNode : classDeclaration.constructors) {
            ModifierLoader.load(constructorNode, List.of(constructorNode.accessModifier), ModifierLoader.ModifierType.CONSTRUCTOR);

            if (
                classDeclaration.constructors.stream()
                    .filter(
                        constructor -> compareParameterTypes(
                            packageLevel,
                            constructor.parameters,
                            constructorNode.parameters.stream()
                                .map(parameter -> parameter.type).toList(),
                            false
                        )
                    ).toList().size() > 1
            ) {
                LoggerFacade.error("Cannot have more than one constructor with the same signature", classDeclaration);
                return;
            }
        }
    }

    private void checkIfAllOverridden(final ClassDeclarationNode classDeclaration, final SymbolTable data) {
        final List<FunctionDeclarationNode> abstractFunctions = getFunctionsByModifier("abstract", classDeclaration, data);
        final List<FunctionDeclarationNode> overriddenFunctions = getFunctionsByModifier("override", classDeclaration, data);

        for (final FunctionDeclarationNode abstractFunction : abstractFunctions) {
            final FunctionDeclarationNode method = findMethodWithParameters(
                data,
                overriddenFunctions,
                abstractFunction.name,
                abstractFunction.parameters.stream()
                    .map(parameter -> parameter.type).toList(),
                true
            );
            if (method == null) {
                LoggerFacade.error("Class '" + classDeclaration.name + "' is not abstract and does not implement abstract base class member '" + abstractFunction.name + "'", classDeclaration);
                return;
            } else if ((method.returnType.isNullable != abstractFunction.returnType.isNullable) ||
                    !data.isSameType(
                        method.returnType,
                        abstractFunction.returnType
                    )
                    && !packageLevel.isSameType(
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
                findMethodWithParameters(
                    data,
                    abstractFunctions,
                    overriddenFunction.name,
                    overriddenFunction.parameters.stream()
                        .map(parameter -> parameter.type).toList(),
                    true
                ) == null
            ) {
                LoggerFacade.error("'" + overriddenFunction.name + "' overrides nothing", overriddenFunction);
                return;
            }
        }
    }

    private void validateBaseClass(ClassDeclarationNode classDeclaration, SymbolTable data) {
        if (!classDeclaration.baseClasses.isEmpty()) {
            if (classDeclaration.baseClasses.size() > 1) {
                LoggerFacade.error("Class '" + classDeclaration.name + "' cannot extend more than one class", classDeclaration);
                return;
            }

            final BaseClassNode baseClassNode = classDeclaration.baseClasses.get(0);
            final ClassDeclarationNode fileLevelBaseClass = data.getClass(baseClassNode.name);
            final ClassDeclarationNode baseClass = getClassDeclarationNode(classDeclaration, baseClassNode.name, fileLevelBaseClass);

            if (baseClass == null || !baseClass.modifiers.contains("open") && !baseClass.modifiers.contains("abstract")) {
                LoggerFacade.error("'" + baseClassNode.name + "' is final, so it cannot be extended", classDeclaration);
                return;
            }

            checkCircularInheritance(classDeclaration.name, baseClass, new HashSet<>(), data);

            final Scope currentScope = new Scope(
                new Scope(null, packageLevel, classDeclaration, Scope.Type.TOP),
                data,
                classDeclaration,
                Scope.Type.TOP
            );

            new ExpressionTraverse().traverse(
                new ExpressionBaseNode(baseClassNode),
                currentScope
            );

            packageLevel.bindingContext().put(baseClassNode, currentScope.getFQName(baseClass));
        }
    }

    private ClassDeclarationNode getClassDeclarationNode(ClassDeclarationNode classDeclaration, String baseClassName, ClassDeclarationNode fileLevelBaseClass) {
        final ClassDeclarationNode packageLevelBaseClass = packageLevel.getClass(baseClassName);
        if (fileLevelBaseClass == null && packageLevelBaseClass == null) {
            LoggerFacade.error("Base class '" + baseClassName + "' for class '" + classDeclaration.name + "' was not found.", classDeclaration);
            return null;
        }

        if (classDeclaration.name.equals(baseClassName)) {
            LoggerFacade.error("Class cannot extend itself: " + classDeclaration.name, classDeclaration);
            return null;
        }

        return fileLevelBaseClass == null ? packageLevelBaseClass : fileLevelBaseClass;
    }

    private void checkCircularInheritance(
        String originalClass,
        ClassDeclarationNode currentClass,
        Set<String> visited,
        SymbolTable data
    ) {
        if (visited.contains(currentClass.name)) {
            LoggerFacade.error("Circular inheritance detected: '" + originalClass + "' -> '" + currentClass.name + "'", currentClass);
            return;
        }

        visited.add(currentClass.name);

        for (BaseClassNode base : currentClass.baseClasses) {
            ClassDeclarationNode nextBase = resolveClass(base.name, data);
            if (nextBase != null) {
                checkCircularInheritance(originalClass, nextBase, new HashSet<>(visited), data);
            }
        }
    }

    private ClassDeclarationNode resolveClass(String className, SymbolTable data) {
        ClassDeclarationNode cls = data.getClass(className);
        return (cls != null) ? cls : packageLevel.getClass(className);
    }

    private void validateInterfaces(List<BaseInterfaceNode> interfaces, SymbolTable data) {
        final Scope currentScope = new Scope(
            new Scope(null, packageLevel, null, Scope.Type.TOP),
            data,
            null,
            Scope.Type.TOP
        );

        for (final BaseInterfaceNode interfaceNode : interfaces) {
            final InterfaceNode baseInterface = currentScope.getInterface(interfaceNode.name);
            if (baseInterface == null) {
                LoggerFacade.error("Interface '" + interfaceNode.name + "' was not found", interfaceNode);
                return;
            }

            data.bindingContext().put(interfaceNode, currentScope.getFQName(baseInterface));
        }
    }

    private void checkCircularInterfaceInheritance(
        String originalInterface,
        InterfaceNode currentInterface,
        Set<String> visited,
        SymbolTable data
    ) {
        if (visited.contains(currentInterface.name)) {
            LoggerFacade.error("Circular inheritance detected: '" + originalInterface + "' -> '" + currentInterface.name + "'", currentInterface);
            return;
        }

        visited.add(currentInterface.name);

        for (BaseInterfaceNode base : currentInterface.implementedInterfaces) {
            InterfaceNode nextInterface = resolveInterface(base.name, data);
            if (nextInterface != null) {
                checkCircularInterfaceInheritance(currentInterface.name, nextInterface, new HashSet<>(visited), data);
            }
        }
    }

    private InterfaceNode resolveInterface(String name, SymbolTable data) {
        InterfaceNode iface = data.getInterface(name);
        return (iface != null) ? iface : packageLevel.getInterface(name);
    }

    private void validateInterfaces(InterfaceNode interfaceNode, SymbolTable data) {
        final Scope currentScope = new Scope(
            new Scope(null, packageLevel, null, Scope.Type.TOP),
            data,
            null,
            Scope.Type.TOP
        );

        for (final BaseInterfaceNode currentInterface : interfaceNode.implementedInterfaces) {
            final InterfaceNode baseInterface = currentScope.getInterface(currentInterface.name);
            if (baseInterface == null) {
                LoggerFacade.error("Interface '" + currentInterface.name + "' was not found", currentInterface);
                return;
            }

            if (currentInterface.name.equals(interfaceNode.name)) {
                LoggerFacade.error("Interface cannot extend itself: " + interfaceNode.name, currentInterface);
                return;
            }

            checkCircularInterfaceInheritance(interfaceNode.name, interfaceNode, new HashSet<>(), data);

            data.bindingContext().put(currentInterface, currentScope.getFQName(baseInterface));
        }
    }

    private void addThisParameterToInstanceMethods(ClassDeclarationNode classDeclaration) {
        for (FunctionDeclarationNode functionDeclaration : classDeclaration.methods) {
            if (!functionDeclaration.modifiers.contains("static")) {
                functionDeclaration.parameters.add(0, new ParameterNode(
                    new FlowType(
                        classDeclaration.name,
                        false,
                        false
                    ),
                    "this",
                    null
                ));
            }
            if (functionDeclaration.modifiers.contains("abstract")) {
                if (!classDeclaration.modifiers.contains("abstract")) {
                    LoggerFacade.error("Abstract function '" + functionDeclaration.name + "' in non-abstract class '" + classDeclaration.name + "'", functionDeclaration);
                    return;
                }
                if (functionDeclaration.block != null) {
                    LoggerFacade.error("Abstract function '" + functionDeclaration.name + "' cannot have a block", functionDeclaration);
                    return;
                }
            } else if (functionDeclaration.block == null) {
                LoggerFacade.error("Function '" + functionDeclaration.name + "' without a body must be abstract", functionDeclaration);
                return;
            }
        }
    }

    private void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable data) {
        ModifierLoader.load(interfaceDeclaration.modifiers, ModifierLoader.ModifierType.INTERFACE);

        validateInterfaces(interfaceDeclaration, data);

        for (FunctionDeclarationNode functionDeclaration : interfaceDeclaration.methods) {
            if (!functionDeclaration.modifiers.contains("static")) {
                functionDeclaration.parameters.add(0, new ParameterNode(
                    new FlowType(
                        interfaceDeclaration.name,
                        false,
                        false
                    ),
                    "this",
                    null
                ));
            }
        }

        data.interfaces().add(interfaceDeclaration);
    }

    private List<FunctionDeclarationNode> getFunctionsByModifier(
        final String modifier,
        final TypeDeclarationNode typeDeclarationNode,
        final SymbolTable data
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
                final ClassDeclarationNode fileLevelBaseClass = data.getClass(baseClassName);
                final ClassDeclarationNode baseClass = getClassDeclarationNode(classDeclarationNode, baseClassName, fileLevelBaseClass);

                if (baseClass == null) {
                    LoggerFacade.error("Unresolved symbol '" + classDeclarationNode.name + "' cannot extend more than one class", classDeclarationNode);
                    return new ArrayList<>();
                }
                foundFunctions.addAll(getFunctionsByModifier(modifier, baseClass, data));
            }
        }

        if (modifier.equals("abstract")) {
            for (final BaseInterfaceNode baseInterfaceNode : typeDeclarationNode.implementedInterfaces) {
                final InterfaceNode interfaceNode = getInterfaceNode(data, baseInterfaceNode);
                if (interfaceNode == null) {
                    return new ArrayList<>();
                }

                foundFunctions.addAll(interfaceNode.methods);
                foundFunctions.addAll(getFunctionsByModifier(modifier, interfaceNode, data));
            }
        }

        return foundFunctions;
    }

    private InterfaceNode getInterfaceNode(SymbolTable data, BaseInterfaceNode baseInterfaceNode) {
        final InterfaceNode fileLevelInterfaceNode = data.getInterface(baseInterfaceNode.name);
        final InterfaceNode packageLevelInterfaceNode = packageLevel.getInterface(baseInterfaceNode.name);

        final InterfaceNode interfaceNode = fileLevelInterfaceNode != null ? fileLevelInterfaceNode : packageLevelInterfaceNode;
        if (interfaceNode == null) {
            LoggerFacade.error("Interface '" + baseInterfaceNode.name + "' was not found", baseInterfaceNode);
            return null;
        }

        return interfaceNode;
    }
}
