package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.VariableAssignmentNode;
import semantic_analysis.files.FileWrapper;

public class VariableDeclarationGenerator {
    public static void generateLocalVariable(
        FieldNode fieldNode,
        MethodVisitor mv,
        VariableManager vm,
        FileWrapper file
    ) {
        final String fieldName = fieldNode.initialization.declaration.name;
        final VariableAssignmentNode assignment = fieldNode.initialization.assignment;
        final FlowType expectedType = fieldNode.initialization.declaration.type;

        if (assignment != null) {
            ExpressionGenerator.generate(assignment.value.expression, mv, vm, file, expectedType);
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }

        vm.declareVariable(fieldName, expectedType, null);
    }

    public static void generateField(
        FieldNode fieldNode,
        ClassWriter cw,
        FileWrapper file,
        TypeDeclarationNode containingType
    ) {
        cw.visitField(
            ModifierMapper.map(fieldNode.modifiers),
            fieldNode.initialization.declaration.name,
            FQNameMapper.getJVMName(fieldNode.initialization.declaration.type, file.scope(), containingType.typeParameters),
            null,
            fieldNode.initialization.declaration.modifier.equals("const")
                ? ((LiteralNode) fieldNode.initialization.assignment.value.expression).getValue()
                : null
        ).visitEnd();
    }
}
