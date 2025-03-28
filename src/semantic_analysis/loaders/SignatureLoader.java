package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.BinaryExpressionNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.util.ArrayList;
import java.util.List;

import static semantic_analysis.scopes.SymbolTable.getFlowPathName;
import static semantic_analysis.scopes.SymbolTable.joinPath;

public class SignatureLoader {
    public static void load(final FileWrapper file, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        for (final ASTNode node : file.root().children) {
            if (node instanceof ClassDeclarationNode classDeclarationNode) {
                handleClass(classDeclarationNode, fileLevel, packageWrapper);
            } else if (node instanceof InterfaceNode interfaceNode) {
                handleInterface(interfaceNode, fileLevel, packageWrapper);
            } else if (node instanceof FunctionDeclarationNode functionDeclarationNode) {
                handleFunction(functionDeclarationNode, fileLevel, packageWrapper, file);
            } else if (node instanceof FieldNode fieldNode) {
                handleField(fieldNode, fileLevel, packageWrapper, file);
            }
        }
    }

    private static void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = ModifierLoader.isPublic(classDeclaration.modifiers);

        if (packageWrapper.scope().findSymbol(classDeclaration.name)) {
            LoggerFacade.error("Symbol '" + classDeclaration.name + "' redefined", classDeclaration);
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

        if (classDeclaration.primaryConstructor.isEmpty() && !classDeclaration.constructors.isEmpty()) {
            return;
        }

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
                    new ExpressionBaseNode(
                        new BinaryExpressionNode(
                            new VariableReferenceNode("this"),
                            new VariableReferenceNode(field.initialization.declaration.name),
                            "."
                        )
                    ),
                    "=",
                    new ExpressionBaseNode(new VariableReferenceNode(field.initialization.declaration.name))
                )
            );
        }

        ConstructorNode primaryConstructor = new ConstructorNode(
            "public",
            primaryParameters,
            new BodyNode(new BlockNode(assignments))
        );

        primaryConstructor.body.scope = new Scope(
            null,
            SymbolTable.getEmptySymbolTable(),
            classDeclaration,
            Scope.Type.FUNCTION
        );

        classDeclaration.constructors.add(primaryConstructor);
    }

    private static void handleInterface(final InterfaceNode interfaceDeclaration, final SymbolTable fileLevel, final PackageWrapper packageWrapper) {
        boolean isPublic = ModifierLoader.isPublic(interfaceDeclaration.modifiers);

        if (packageWrapper.scope().findSymbol(interfaceDeclaration.name)) {
            LoggerFacade.error("Symbol '" + interfaceDeclaration.name + "' redefined", interfaceDeclaration);
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

    private static void handleFunction(
        final FunctionDeclarationNode functionDeclarationNode,
        final SymbolTable fileLevel,
        final PackageWrapper packageWrapper,
        final FileWrapper file
    ) {
        boolean isPublic = ModifierLoader.isPublic(functionDeclarationNode.modifiers);

        if (packageWrapper.scope().findSymbol(functionDeclarationNode.name)) {
            LoggerFacade.error("Symbol '" + functionDeclarationNode.name + "' redefined", functionDeclarationNode);
        }

        if (isPublic) {
            if (ModifierLoader.isDefaultPublic(functionDeclarationNode.modifiers)) {
                functionDeclarationNode.modifiers.add("public");
            }

            packageWrapper.scope().symbols().functions().add(functionDeclarationNode);
            packageWrapper.scope().symbols().bindingContext().put(functionDeclarationNode, getFlowPathName(packageWrapper.path(), file.name()));
        } else {
            fileLevel.functions().add(functionDeclarationNode);
        }
    }

    private static void handleField(
        final FieldNode fieldNode,
        final SymbolTable fileLevel,
        final PackageWrapper packageWrapper,
        final FileWrapper file
    ) {
        boolean isPublic = ModifierLoader.isPublic(fieldNode.modifiers);

        if (fieldNode.initialization == null) {
            return;
        }

        final String name = fieldNode.initialization.declaration.name;
        if (packageWrapper.scope().findSymbol(name)) {
            LoggerFacade.error("Symbol '" + fieldNode.initialization.declaration.name + "' redefined", fieldNode);
        }

        if (isPublic) {
            if (ModifierLoader.isDefaultPublic(fieldNode.modifiers)) {
                fieldNode.modifiers.add("public");
            }

            packageWrapper.scope().symbols().fields().add(fieldNode);
            packageWrapper.scope().symbols().bindingContext().put(fieldNode, getFlowPathName(packageWrapper.path(), file.name()));
        } else {
            fileLevel.fields().add(fieldNode);
        }
    }
}