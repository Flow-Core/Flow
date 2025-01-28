package semantic_analysis.loaders;

import logger.Logger;
import logger.LoggerFacade;
import parser.ASTMetaDataStore;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    }

    private void validateBaseClass(ClassDeclarationNode classDeclaration, SymbolTable data) {
        if (!classDeclaration.baseClasses.isEmpty()) {
            if (classDeclaration.baseClasses.size() > 1) {
                LoggerFacade.getLogger().log(
                    Logger.Severity.ERROR,
                    "Class '" + classDeclaration.name + "' cannot extend more than one class",
                    ASTMetaDataStore.getInstance().getLine(classDeclaration),
                    ASTMetaDataStore.getInstance().getFile(classDeclaration)
                );
            }

            final String baseClassName = classDeclaration.baseClasses.get(0).name;
            final ClassDeclarationNode fileLevelBaseClass = data.getClass(baseClassName);
            final ClassDeclarationNode baseClass = getClassDeclarationNode(classDeclaration, baseClassName, fileLevelBaseClass);

            if (baseClass != null) {
                checkCircularInheritance(classDeclaration.name, baseClass, new HashSet<>(), data);
            }
        }
    }

    private ClassDeclarationNode getClassDeclarationNode(ClassDeclarationNode classDeclaration, String baseClassName, ClassDeclarationNode fileLevelBaseClass) {
        final ClassDeclarationNode packageLevelBaseClass = packageLevel.getClass(baseClassName);
        if (fileLevelBaseClass == null && packageLevelBaseClass == null) {
            LoggerFacade.getLogger().log(
                Logger.Severity.ERROR,
                "Base class '" + baseClassName + "' for class '" + classDeclaration.name + "' was not found.",
                ASTMetaDataStore.getInstance().getLine(classDeclaration),
                ASTMetaDataStore.getInstance().getFile(classDeclaration)
            );
        }

        if (classDeclaration.name.equals(baseClassName)) {
            LoggerFacade.getLogger().log(
                Logger.Severity.ERROR,
                "Class cannot extend itself: " + classDeclaration.name,
                ASTMetaDataStore.getInstance().getLine(classDeclaration),
                ASTMetaDataStore.getInstance().getFile(classDeclaration)
            );
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
            LoggerFacade.getLogger().log(
                Logger.Severity.ERROR,
                "Circular inheritance detected: " + originalClass + " -> " + currentClass.name,
                ASTMetaDataStore.getInstance().getLine(currentClass),
                ASTMetaDataStore.getInstance().getFile(currentClass)
            );
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
        for (final BaseInterfaceNode interfaceNode : interfaces) {
            if (data.getInterface(interfaceNode.name) == null && packageLevel.getInterface(interfaceNode.name) == null) {
                LoggerFacade.getLogger().log(
                    Logger.Severity.ERROR,
                    "Interface '" + interfaceNode.name + "' was not found.",
                    ASTMetaDataStore.getInstance().getLine(interfaceNode),
                    ASTMetaDataStore.getInstance().getFile(interfaceNode)
                );
                return;
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
            LoggerFacade.getLogger().log(
                Logger.Severity.ERROR,
                "Circular inheritance detected: " + currentInterface.name + " -> " + originalInterface,
                ASTMetaDataStore.getInstance().getLine(currentInterface),
                ASTMetaDataStore.getInstance().getFile(currentInterface)
            );
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
        for (final BaseInterfaceNode currentInterface : interfaceNode.implementedInterfaces) {
            if (data.getInterface(currentInterface.name) == null && packageLevel.getInterface(currentInterface.name) == null) {
                LoggerFacade.getLogger().log(
                    Logger.Severity.ERROR,
                    "Interface '" + currentInterface.name + "' was not found.",
                    ASTMetaDataStore.getInstance().getLine(currentInterface),
                    ASTMetaDataStore.getInstance().getFile(currentInterface)
                );
                return;
            }

            if (currentInterface.name.equals(interfaceNode.name)) {
                LoggerFacade.getLogger().log(
                    Logger.Severity.ERROR,
                    "Interface cannot extend itself: " + interfaceNode.name,
                    ASTMetaDataStore.getInstance().getLine(interfaceNode),
                    ASTMetaDataStore.getInstance().getFile(interfaceNode)
                );
                return;
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
                functionDeclaration.parameters.add(0, new ParameterNode(classDeclaration.name, "this", null));
            }
        }
    }

    private void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable data) {
        validateInterfaces(interfaceDeclaration, data);

        for (FunctionDeclarationNode functionDeclaration : interfaceDeclaration.methods) {
            if (!functionDeclaration.modifiers.contains("static")) {
                functionDeclaration.parameters.add(0, new ParameterNode(interfaceDeclaration.name, "this", null));
            }
        }
    }
}
