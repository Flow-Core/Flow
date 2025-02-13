package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.ObjectNode;
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
        } else if (statementNode instanceof ThrowNode throwNode) {
            ExpressionGenerator.generate(throwNode.throwValue.expression, mv, vm, file);

            mv.visitInsn(Opcodes.ATHROW);
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
        if (returnStatementNode.returnValue.expression instanceof ObjectNode objectNode && objectNode.className.equals("Void")) {
            mv.visitInsn(Opcodes.RETURN);
            return;
        }

        ExpressionGenerator.generate(returnStatementNode.returnValue.expression, mv, vm, file);

        mv.visitInsn(Opcodes.ARETURN);
    }

    private static void generateForStatement(ForStatementNode forStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label startLabel = new Label();
        final Label endLabel = new Label();

        VariableDeclarationGenerator.generateLocalVariable(forStatementNode.populatedInitialization, mv, vm, file);

        mv.visitLabel(startLabel);

        ExpressionGenerator.generate(forStatementNode.condition.expression, mv, vm, file);
        mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Bool", "value", "Z");
        mv.visitJumpInsn(Opcodes.IFNE, endLabel);

        BlockGenerator.generateFunctionBlock(forStatementNode.loopBlock, file, mv, vm);
        BlockGenerator.generateFunctionBlock(forStatementNode.action, file, mv, vm);

        mv.visitJumpInsn(Opcodes.GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    private static void generateTryStatement(TryStatementNode tryStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label tryStart = new Label();
        final Label tryEnd = new Label();
        final Label finallyStart = new Label();
        final Label finallyEnd = new Label();

        mv.visitLabel(tryStart);
        BlockGenerator.generateFunctionBlock(tryStatementNode.tryBranch, file, mv, vm);
        mv.visitLabel(tryEnd);

        if (tryStatementNode.finallyBranch != null) {
            mv.visitJumpInsn(Opcodes.GOTO, finallyStart);
        }

        for (CatchNode catchNode : tryStatementNode.exceptionBranches) {
            Label catchLabel = new Label();

            mv.visitTryCatchBlock(
                tryStart,
                tryEnd,
                catchLabel,
                FQNameMapper.getFQName(catchNode.parameter.type, file.scope())
            );

            mv.visitLabel(catchLabel);

            vm.declareVariable(catchNode.parameter.name);

            BlockGenerator.generateFunctionBlock(catchNode.body, file, mv, vm);

            if (tryStatementNode.finallyBranch != null) {
                mv.visitJumpInsn(Opcodes.GOTO, finallyStart);
            }
        }

        if (tryStatementNode.finallyBranch != null) {
            mv.visitTryCatchBlock(tryStart, tryEnd, finallyStart, null);
            mv.visitLabel(finallyStart);
            BlockGenerator.generateFunctionBlock(tryStatementNode.finallyBranch, file, mv, vm);
            mv.visitLabel(finallyEnd);
        }
    }

    private static void generateSwitchStatement(SwitchStatementNode switchStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        // TODO: finish
    }
}