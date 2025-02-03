package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.classes.*;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        validateBaseClass(classDeclaration, data);
        validateInterfaces(classDeclaration.implementedInterfaces, data);

        addPrimaryConstructor(classDeclaration);
        addThisParameterToInstanceMethods(classDeclaration);

        if (!classDeclaration.modifiers.contains("abstract")) {
            checkIfAllOverridden(classDeclaration, data);
        }

        data.classes().add(classDeclaration);
    }

    private void checkIfAllOverridden(final ClassDeclarationNode classDeclaration, final SymbolTable data) {
        // TODO: ignore this parameter

        final List<FunctionDeclarationNode> abstractFunctions = getFunctionsByModifier("abstract", classDeclaration, data);
        final List<FunctionDeclarationNode> overriddenFunctions = getFunctionsByModifier("override", classDeclaration, data);

        for (final FunctionDeclarationNode abstractFunction : abstractFunctions) {
            final FunctionDeclarationNode method = findMethodWithParameters(
                overriddenFunctions,
                abstractFunction.name,
                abstractFunction.parameters.stream().map(functionNode -> functionNode.name).toList()
            );
            if (method == null) {
                throw new SA_SemanticError("Class '" + classDeclaration.name + "' is not abstract and does not implement abstract base class member '" + abstractFunction.name + "'");
            } else if (!method.returnType.equals(abstractFunction.returnType)) {
                throw new SA_SemanticError("Return type of function '" + abstractFunction.name + "' does not have the same return type as the overridden class, expected: '" + abstractFunction.returnType + "' but found '" + method.returnType + "'");
            }
        }

        for (final FunctionDeclarationNode overriddenFunction : overriddenFunctions) {
            if (
                findMethodWithParameters(
                    abstractFunctions,
                    overriddenFunction.name,
                    overriddenFunction.parameters.stream().map(functionNode -> functionNode.name).toList()
                ) == null
            ) {
                throw new SA_SemanticError("'" + overriddenFunction.name + "' overrides nothing");
            }
        }
    }

    private void validateBaseClass(ClassDeclarationNode classDeclaration, SymbolTable data) {
        if (!classDeclaration.baseClasses.isEmpty()) {
            if (classDeclaration.baseClasses.size() > 1) {
                throw new SA_SemanticError("Class '" + classDeclaration.name + "' cannot extend more than one class.");
            }

            final String baseClassName = classDeclaration.baseClasses.get(0).name;
            final ClassDeclarationNode fileLevelBaseClass = data.getClass(baseClassName);
            final ClassDeclarationNode baseClass = getClassDeclarationNode(classDeclaration, baseClassName, fileLevelBaseClass);
            checkCircularInheritance(classDeclaration.name, baseClass, new HashSet<>(), data);
        }
    }

    private ClassDeclarationNode getClassDeclarationNode(ClassDeclarationNode classDeclaration, String baseClassName, ClassDeclarationNode fileLevelBaseClass) {
        final ClassDeclarationNode packageLevelBaseClass = packageLevel.getClass(baseClassName);
        if (fileLevelBaseClass == null && packageLevelBaseClass == null) {
            throw new SA_SemanticError("Base class '" + baseClassName + "' for class '" + classDeclaration.name + "' was not found.");
        }

        if (classDeclaration.name.equals(baseClassName)) {
            throw new SA_SemanticError("Class cannot extend itself: " + classDeclaration.name);
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
            throw new SA_SemanticError(
                "Circular inheritance detected: " + originalClass + " -> " + currentClass.name
            );
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
        for (final BaseInterfaceNode interfaceNode : interfaces) {
            if (data.getInterface(interfaceNode.name) == null && packageLevel.getInterface(interfaceNode.name) == null) {
                throw new SA_SemanticError("Interface '" + interfaceNode.name + "' was not found.");
            }
        }
    }

    private void checkCircularInterfaceInheritance(
        String originalInterface,
        InterfaceNode currentInterface,
        Set<String> visited,
        SymbolTable data
    ) {
        if (visited.contains(currentInterface.name)) {
            throw new SA_SemanticError(
                "Circular inheritance detected: " + currentInterface.name + " -> " + originalInterface
            );
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
        for (final BaseInterfaceNode currentInterface : interfaceNode.implementedInterfaces) {
            if (data.getInterface(currentInterface.name) == null && packageLevel.getInterface(currentInterface.name) == null) {
                throw new SA_SemanticError("Interface '" + currentInterface.name + "' was not found");
            }

            if (currentInterface.name.equals(interfaceNode.name)) {
                throw new SA_SemanticError("Interface cannot extend itself: " + interfaceNode.name);
            }

            checkCircularInterfaceInheritance(interfaceNode.name, interfaceNode, new HashSet<>(), data);
        }
    }

    private void addPrimaryConstructor(ClassDeclarationNode classDeclaration) {
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
                    field.initialization.declaration.name,
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

    private void addThisParameterToInstanceMethods(ClassDeclarationNode classDeclaration) {
        for (FunctionDeclarationNode functionDeclaration : classDeclaration.methods) {
            if (!functionDeclaration.modifiers.contains("static")) {
                functionDeclaration.parameters.add(0, new ParameterNode(classDeclaration.name, false, "this", null));
            }
            if (functionDeclaration.modifiers.contains("abstract")) {
                if (!classDeclaration.modifiers.contains("abstract")) {
                    throw new SA_SemanticError("Abstract function '" + functionDeclaration.name + "' in non-abstract class '" + classDeclaration.name + "'");
                }
                if (functionDeclaration.block != null) {
                    throw new SA_SemanticError("Abstract function '" + functionDeclaration.name + "' cannot have a block");
                }
            } else if (functionDeclaration.block == null) {
                throw new SA_SemanticError("Function '" + functionDeclaration.name + "' without a body must be abstract");
            }
        }
    }

    private void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable data) {
        validateInterfaces(interfaceDeclaration, data);

        for (FunctionDeclarationNode functionDeclaration : interfaceDeclaration.methods) {
            if (!functionDeclaration.modifiers.contains("static")) {
                functionDeclaration.parameters.add(0, new ParameterNode(interfaceDeclaration.name, false, "this", null));
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
                foundFunctions.addAll(getFunctionsByModifier(modifier, baseClass, data));
            }
        }

        if (modifier.equals("abstract")) {
            for (final BaseInterfaceNode baseInterfaceNode : typeDeclarationNode.implementedInterfaces) {
                final InterfaceNode interfaceNode = getInterfaceNode(data, baseInterfaceNode);

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
            throw new SA_SemanticError("Interface '" + baseInterfaceNode.name + "' was not found");
        }

        return interfaceNode;
    }
}
