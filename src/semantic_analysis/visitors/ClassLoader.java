package semantic_analysis.visitors;

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
import java.util.List;

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
            interfaceHandler(interfaceDeclaration, data);
        }
    }

    private void handleClass(final ClassDeclarationNode classDeclaration, final SymbolTable data) {
        if (!classDeclaration.baseClasses.isEmpty()) {
            if (classDeclaration.baseClasses.size() > 1) {
                throw new SA_SemanticError("Only one class can appear in a supertype list");
            }
            if (!data.findClass(classDeclaration.baseClasses.get(0).name) && !packageLevel.findClass(classDeclaration.baseClasses.get(0).name)) {
                throw new SA_SemanticError("Class '" + classDeclaration.baseClasses.get(0).name + "' was not found");
            }
        }
        for (final BaseInterfaceNode interfaceNode : classDeclaration.interfaces) {
            if (!data.findInterface(interfaceNode.name) && !packageLevel.findInterface(interfaceNode.name)) {
                throw new SA_SemanticError("Interface '" + interfaceNode.name + "' was not found");
            }
        }

        List<ParameterNode> primaryParameters = new ArrayList<>();
        List<ASTNode> assignments = new ArrayList<>();

        // Add the primary constructor fields
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

        classDeclaration.constructors.add(
            new ConstructorNode(
                "public",
                primaryParameters,
                new BlockNode(
                    assignments
                )
            )
        );

        for (FunctionDeclarationNode functionDeclarationNode : classDeclaration.methods) {
            if (!functionDeclarationNode.modifiers.contains("static")) {
                functionDeclarationNode.parameters.add(0, new ParameterNode(classDeclaration.name, "this", null));
            }
        }
    }

    private void interfaceHandler(final InterfaceNode interfaceNode, final SymbolTable data) {
        for (final BaseInterfaceNode baseInterfaceNode : interfaceNode.implementedInterfaces) {
            if (!data.findInterface(interfaceNode.name) && !packageLevel.findInterface(interfaceNode.name)) {
                throw new SA_SemanticError("Interface '" + baseInterfaceNode.name + "' was not found");
            }
        }

        for (FunctionDeclarationNode functionDeclarationNode : interfaceNode.methods) {
            if (!functionDeclarationNode.modifiers.contains("static")) {
                functionDeclarationNode.parameters.add(0, new ParameterNode(interfaceNode.name, "this", null));
            }
        }
    }
}