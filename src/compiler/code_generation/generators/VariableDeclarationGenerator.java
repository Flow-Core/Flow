package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.FieldNode;
import parser.nodes.literals.LiteralNode;
import parser.nodes.variable.VariableAssignmentNode;
import semantic_analysis.files.FileWrapper;

import static compiler.code_generation.generators.FunctionGenerator.getJVMName;

public class VariableDeclarationGenerator {
    public static void generateLocalVariable(
        FieldNode fieldNode,
        MethodVisitor mv,
        VariableManager vm,
        FileWrapper file
    ) {
        final String fieldName = fieldNode.initialization.declaration.name;
        final VariableAssignmentNode assignment = fieldNode.initialization.assignment;

        if (assignment != null) {
            ExpressionGenerator.generate(assignment.value.expression, mv, vm, file, fieldNode.initialization.declaration.type);
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }

        vm.declareVariable(fieldName, fieldNode.initialization.declaration.type);
    }

    public static void generateField(
        FieldNode fieldNode,
        ClassWriter cw,
        FileWrapper file
    ) {
        cw.visitField(
            ModifierMapper.map(fieldNode.modifiers),
            fieldNode.initialization.declaration.name,
            getJVMName(fieldNode.initialization.declaration.type, file.scope()),
            null,
            fieldNode.initialization.declaration.modifier.equals("const")
                ? ((LiteralNode) fieldNode.initialization.assignment.value.expression).getValue()
                : null
        ).visitEnd();
    }
}
