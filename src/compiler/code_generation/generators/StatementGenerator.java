package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;
import parser.nodes.classes.ObjectNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.statements.*;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.scopes.Scope;

public class StatementGenerator {
    public static void generate(StatementNode statementNode, MethodVisitor mv, VariableManager vm, FileWrapper file, Scope currentScope) {
        if (statementNode instanceof IfStatementNode ifStatementNode) {
            generateIfStatement(ifStatementNode, mv, vm, file);
        } else if (statementNode instanceof WhileStatementNode whileStatementNode) {
            generateWhileStatement(whileStatementNode, mv, vm, file);
        } else if (statementNode instanceof ForStatementNode forStatementNode) {
            generateForStatement(forStatementNode, mv, vm, file);
        } else if (statementNode instanceof ReturnStatementNode returnStatementNode) {
            generateReturnStatement(returnStatementNode, mv, vm, file, currentScope);
        } else if (statementNode instanceof TryStatementNode tryStatementNode) {
            generateTryStatement(tryStatementNode, mv, vm, file);
        } else if (statementNode instanceof SwitchStatementNode switchStatementNode) {
            generateSwitchStatement(switchStatementNode, mv, vm, file);
        } else if (statementNode instanceof ThrowNode throwNode) {
            ExpressionGenerator.generate(throwNode.throwValue.expression, mv, vm, file, new FlowType("Throwable", true, false));

            mv.visitInsn(Opcodes.ATHROW);
        } else {
            throw new UnsupportedOperationException("Unknown statement");
        }
    }

    private static void generateIfStatement(IfStatementNode ifStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label elseLabel = new Label();
        final Label endLabel = new Label();

        ExpressionGenerator.generate(ifStatementNode.condition.expression, mv, vm, file, new FlowType("Bool", false, true));
        mv.visitJumpInsn(Opcodes.IFEQ, elseLabel);

        BlockGenerator.generateFunctionBlock(ifStatementNode.trueBranch.scope, ifStatementNode.trueBranch.blockNode, file, mv, vm);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        mv.visitLabel(elseLabel);
        if (ifStatementNode.falseBranch != null && ifStatementNode.falseBranch.blockNode != null) {
            BlockGenerator.generateFunctionBlock(ifStatementNode.falseBranch.scope, ifStatementNode.falseBranch.blockNode, file, mv, vm);
        }

        mv.visitLabel(endLabel);
    }

    private static void generateWhileStatement(WhileStatementNode whileStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label startLabel = new Label();
        final Label endLabel = new Label();

        mv.visitLabel(startLabel);

        ExpressionGenerator.generate(whileStatementNode.condition.expression, mv, vm, file, new FlowType("Bool", false, true));
        mv.visitJumpInsn(Opcodes.IFEQ, endLabel);

        BlockGenerator.generateFunctionBlock(whileStatementNode.loopBlock.scope, whileStatementNode.loopBlock.blockNode, file, mv, vm);

        mv.visitJumpInsn(Opcodes.GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    private static void generateReturnStatement(ReturnStatementNode returnStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file, Scope currentScope) {
        if (returnStatementNode.returnValue.expression instanceof ObjectNode objectNode && objectNode.type.name.equals("Void")) {
            mv.visitInsn(Opcodes.RETURN);
            return;
        }

        FunctionDeclarationNode functionDeclarationNode = (FunctionDeclarationNode) currentScope.currentParent();
        FlowType expectedReturnType = functionDeclarationNode.returnType;
        ExpressionGenerator.generate(returnStatementNode.returnValue.expression, mv, vm, file, expectedReturnType);

        mv.visitInsn(getReturnOpcode(expectedReturnType));
    }

    private static int getReturnOpcode(FlowType returnType) {
        if (returnType.isPrimitive) {
            return switch (returnType.name) {
                case "Int", "Bool", "Byte", "Short", "Char" -> Opcodes.IRETURN;
                case "Float" -> Opcodes.FRETURN;
                case "Double" -> Opcodes.DRETURN;
                case "Long" -> Opcodes.LRETURN;
                default -> Opcodes.ARETURN;
            };
        }
        return Opcodes.ARETURN;
    }

    private static void generateForStatement(ForStatementNode forStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label startLabel = new Label();
        final Label endLabel = new Label();

        VariableDeclarationGenerator.generateLocalVariable(forStatementNode.populatedInitialization, mv, vm, file);

        mv.visitLabel(startLabel);

        ExpressionGenerator.generate(forStatementNode.condition.expression, mv, vm, file, new FlowType("Bool", false, true));
        mv.visitJumpInsn(Opcodes.IFEQ, endLabel);

        BlockGenerator.generateFunctionBlock(forStatementNode.loopBlock.scope, forStatementNode.loopBlock.blockNode, file, mv, vm);
        BlockGenerator.generateFunctionBlock(forStatementNode.action.scope, forStatementNode.action.blockNode, file, mv, vm);

        mv.visitJumpInsn(Opcodes.GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    private static void generateTryStatement(TryStatementNode tryStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        final Label tryStart = new Label();
        final Label tryEnd = new Label();
        final Label finallyStart = new Label();
        final Label finallyEnd = new Label();

        mv.visitLabel(tryStart);
        BlockGenerator.generateFunctionBlock(tryStatementNode.tryBranch.scope, tryStatementNode.tryBranch.blockNode, file, mv, vm);
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
                FQNameMapper.getFQName(catchNode.parameter.type.name, file.scope())
            );

            mv.visitLabel(catchLabel);

            vm.declareVariable(catchNode.parameter.name, catchNode.parameter.type, new FlowType("Throwable", false, false));

            BlockGenerator.generateFunctionBlock(catchNode.body.scope, catchNode.body.blockNode, file, mv, vm);

            if (tryStatementNode.finallyBranch != null) {
                mv.visitJumpInsn(Opcodes.GOTO, finallyStart);
            }
        }

        if (tryStatementNode.finallyBranch != null) {
            mv.visitTryCatchBlock(tryStart, tryEnd, finallyStart, null);
            mv.visitLabel(finallyStart);
            BlockGenerator.generateFunctionBlock(tryStatementNode.finallyBranch.scope, tryStatementNode.finallyBranch.blockNode, file, mv, vm);
            mv.visitLabel(finallyEnd);
        }
    }

    private static void generateSwitchStatement(SwitchStatementNode switchStatementNode, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        // TODO: finish
    }
}