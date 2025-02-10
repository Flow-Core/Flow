package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.statements.*;
import semantic_analysis.files.FileWrapper;

public class StatementGenerator {
    public static void generate(StatementNode statementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        if (statementNode instanceof IfStatementNode ifStatementNode) {
            generateIfStatement(ifStatementNode, mv, vm, file);
        } else if (statementNode instanceof WhileStatementNode whileStatementNode) {
            generateWhileStatement(whileStatementNode, mv, vm, file);
        } else if (statementNode instanceof ForStatementNode forStatementNode) {
            generateForStatement(forStatementNode, mv, vm, file);
        } else if (statementNode instanceof ReturnStatementNode returnStatementNode) {
            generateReturnStatement(returnStatementNode, mv, vm, file);
        } else if (statementNode instanceof TryStatementNode tryStatementNode) {
            generateTryStatement(tryStatementNode, mv, vm, file);
        } else if (statementNode instanceof SwitchStatementNode switchStatementNode) {
            generateSwitchStatement(switchStatementNode, mv, vm, file);
        } else {
            throw new UnsupportedOperationException("Unknown statement");
        }
    }

    private static void generateIfStatement(IfStatementNode ifStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label elseLabel = new Label();
        final Label endLabel = new Label();

        ExpressionGenerator.generate(ifStatementNode.condition.expression, mv, vm, file);

        mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Bool", "value", "Z");
        mv.visitJumpInsn(Opcodes.IFNE, elseLabel);

        BlockGenerator.generateFunctionBlock(ifStatementNode.trueBranch, file, mv, vm);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        mv.visitLabel(elseLabel);
        if (ifStatementNode.falseBranch != null) {
            BlockGenerator.generateFunctionBlock(ifStatementNode.falseBranch, file, mv, vm);
        }

        mv.visitLabel(endLabel);
    }

    private static void generateWhileStatement(WhileStatementNode whileStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label startLabel = new Label();
        final Label endLabel = new Label();

        mv.visitLabel(startLabel);

        ExpressionGenerator.generate(whileStatementNode.condition.expression, mv, vm, file);

        mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Bool", "value", "Z");
        mv.visitJumpInsn(Opcodes.IFNE, endLabel);

        BlockGenerator.generateFunctionBlock(whileStatementNode.loopBlock, file, mv, vm);

        mv.visitJumpInsn(Opcodes.GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    private static void generateReturnStatement(ReturnStatementNode returnStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        ExpressionGenerator.generate(returnStatementNode.returnValue.expression, mv, vm, file);

        mv.visitInsn(Opcodes.ARETURN);
    }

    private static void generateForStatement(ForStatementNode forStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        // TODO: finish
    }

    private static void generateTryStatement(TryStatementNode tryStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        // TODO: finish
    }

    private static void generateSwitchStatement(SwitchStatementNode switchStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        // TODO: finish
    }
}