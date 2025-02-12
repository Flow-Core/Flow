package compiler.code_generation.generators;

import compiler.code_generation.CodeGeneration;
import compiler.code_generation.manager.VariableManager;
import org.objectweb.asm.MethodVisitor;
import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.components.BlockNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.statements.StatementNode;
import parser.nodes.variable.VariableAssignmentNode;
import semantic_analysis.files.FileWrapper;

import java.util.ArrayList;
import java.util.List;

public class BlockGenerator {
    public static void generateFunctionBlock(BlockNode blockNode, FileWrapper file, MethodVisitor mv, VariableManager vm) {
        for (final ASTNode node : blockNode.children) {
            if (node instanceof ExpressionBaseNode expressionBaseNode) {
                ExpressionGenerator.generate(expressionBaseNode.expression, mv, vm, file);
            } else if (node instanceof ExpressionNode expressionNode) {
                ExpressionGenerator.generate(expressionNode, mv, vm, file);
            } else if (node instanceof StatementNode statementNode) {
                StatementGenerator.generate(statementNode, mv, vm, file);
            } else if (node instanceof FieldNode fieldNode) {
                VariableDeclarationGenerator.generateLocalVariable(fieldNode, mv, vm, file);
            } else if (node instanceof VariableAssignmentNode variableAssignmentNode) {
                VariableAssignmentGenerator.generate(variableAssignmentNode, mv, vm, file);
            } else {
                throw new UnsupportedOperationException("Invalid operation");
            }
        }
    }

    public static List<CodeGeneration.ClassFile> generateClassBlock(BlockNode blockNode, FileWrapper file) {
        final List<CodeGeneration.ClassFile> classes = new ArrayList<>();

        for (final ASTNode node : blockNode.children) {
            if (node instanceof ClassDeclarationNode classDeclarationNode) {
                classes.addAll(ClassGenerator.generate(classDeclarationNode, file));
            } else if (node instanceof InterfaceNode interfaceNode) {
                classes.addAll(InterfaceGenerator.generate(interfaceNode, file));
            }
        }

        return classes;
    }
}
