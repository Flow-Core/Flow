package compiler.code_generation.generators;

import compiler.code_generation.mappers.FQNameMapper;
import compiler.packer.PackerFacade;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeArgument;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.visitors.ParameterTraverse;

import java.util.List;

public class MainGenerator {
    public static void generate(ClassWriter cw, ClassDeclarationNode topLevelClass, FileWrapper file) {
        boolean isArrayDescriptor = false;
        FunctionDeclarationNode main = ParameterTraverse.findMethodWithParameters(file.scope(), topLevelClass.methods, "main", List.of());

        if (
            main == null
                || !main.modifiers.contains("static")
                || !main.modifiers.contains("public")
                || !main.returnType.equals(new FlowType("Void", false, true))
        ) {
            FlowType stringType = new FlowType("String", false, false);
            FlowType argsArray = new FlowType("flow.collections.Array", false, false, List.of(new TypeArgument(stringType)));

            main = ParameterTraverse.findMethodWithParameters(
                file.scope(),
                topLevelClass.methods,
                "main",
                List.of(argsArray)
            );

            isArrayDescriptor = true;

            if (
                main == null
                    || !main.modifiers.contains("static")
                    || !main.modifiers.contains("public")
                    || !main.returnType.equals(new FlowType("Void", false, true))
            ) {
                return;
            }
        }

        PackerFacade.setMainClassFQName(
            FQNameMapper.getFQName(topLevelClass, file.scope())
        );

        final MethodVisitor mv = cw.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            "main",
            "([Ljava/lang/String;)V",
            null,
            null
        );

        mv.visitCode();

        if (!isArrayDescriptor) {
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                FQNameMapper.getFQName(topLevelClass.name, file.scope()),
                "main",
                "()V",
                false
            );
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "flow/collections/Array",
                "fromPrimitiveArray",
                "([Ljava/lang/Object;)Lflow/collections/Array;",
                false
            );

            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                FQNameMapper.getFQName(topLevelClass.name, file.scope()),
                "main",
                "(Lflow/collections/Array;)V",
                false
            );
        }

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
