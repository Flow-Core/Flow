package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.files.FileWrapper;

public class VariableAssignmentGenerator {
    public static void generate(
        VariableAssignmentNode variableAssignmentNode,
        MethodVisitor mv,
        VariableManager vm,
        FileWrapper file
    ) {
        final ExpressionNode variable = variableAssignmentNode.variable.expression;
        if (variable instanceof VariableReferenceNode variableReferenceNode) {
            ExpressionGenerator.generate(variableAssignmentNode.value.expression, mv, vm, file);
            mv.visitVarInsn(Opcodes.ASTORE, vm.loadVariable(variableReferenceNode.variable));
        } else if (variable instanceof FieldReferenceNode fieldReferenceNode) {
            int opcode = Opcodes.PUTFIELD;
            final String holderFQName = FQNameMapper.getFQName(fieldReferenceNode.holderType, file.scope());
            final String typeFQName = FQNameMapper.getFQName(fieldReferenceNode.type.type(), file.scope());

            if (fieldReferenceNode.holder == null) {
                opcode = Opcodes.PUTSTATIC;
            }

            ExpressionGenerator.generate(fieldReferenceNode.holder, mv, vm, file);
            ExpressionGenerator.generate(variableAssignmentNode.value.expression, mv, vm, file);
            mv.visitFieldInsn(opcode, holderFQName, fieldReferenceNode.name, typeFQName);
        } else {
            throw new UnsupportedOperationException("Invalid variable assignment expression");
        }
    }
}
