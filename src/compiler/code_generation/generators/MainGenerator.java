package compiler.code_generation.generators;

import compiler.code_generation.mappers.FQNameMapper;
import compiler.packer.PackerFacade;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.visitors.ParameterTraverse;

import java.util.List;

public class MainGenerator {
    public static void generate(ClassWriter cw, ClassDeclarationNode topLevelClass, FileWrapper file) {
        FunctionDeclarationNode main = ParameterTraverse.findMethodWithParameters(file.scope(), topLevelClass.methods, "main", List.of());
        if (
            main == null
                || !main.modifiers.contains("static")
                || !main.modifiers.contains("public")
                || !main.returnType.name.equals("Void")
                || main.returnType.isNullable
        ) {
            return;
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

        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            FQNameMapper.getFQName(topLevelClass.name, file.scope()),
            "main",
            "()V",
            false
        );

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
