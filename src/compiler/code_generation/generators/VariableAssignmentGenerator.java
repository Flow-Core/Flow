package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;

public class VariableAssignmentGenerator {
    public static void generate(
        VariableAssignmentNode variableAssignmentNode,
        MethodVisitor mv,
        VariableManager vm,
        FileWrapper file,
        Scope scope
    ) {
        final ExpressionNode variable = variableAssignmentNode.variable.expression;
        if (variable instanceof VariableReferenceNode variableReferenceNode) {
            final FieldNode fieldNode = TypeRecognize.getField(variableReferenceNode.variable, scope);
            if (fieldNode == null) {
                throw new IllegalArgumentException("Variable is not loaded in the current scope");
            }

            ExpressionGenerator.generate(variableAssignmentNode.value.expression, mv, vm, file, fieldNode.initialization.declaration.type);

            vm.storeVariable(variableReferenceNode.variable, null);
        } else if (variable instanceof FieldReferenceNode fieldReferenceNode) {
            final ClassDeclarationNode holder = TypeRecognize.getClass(fieldReferenceNode.holderType.name, scope);
            if (holder == null) {
                throw new IllegalArgumentException("Holder is not loaded in the current scope");
            }

            final FieldNode fieldNode = holder.findField(scope, fieldReferenceNode.name);

            int opcode = Opcodes.PUTFIELD;
            final String holderFQName = FQNameMapper.getFQName(fieldReferenceNode.holderType.name, scope);
            final String descriptor = FQNameMapper.getJVMName(fieldReferenceNode.type, file.scope(), holder.typeParameters);

            if (fieldReferenceNode.isStatic) {
                opcode = Opcodes.PUTSTATIC;
            } else {
                ExpressionGenerator.generate(fieldReferenceNode.holder, mv, vm, file, fieldReferenceNode.holderType);
            }

            ExpressionGenerator.generate(variableAssignmentNode.value.expression, mv, vm, file, fieldNode.initialization.declaration.type);
            mv.visitFieldInsn(opcode, holderFQName, fieldReferenceNode.name, descriptor);
        } else {
            throw new UnsupportedOperationException("Invalid variable assignment expression");
        }
    }
}
