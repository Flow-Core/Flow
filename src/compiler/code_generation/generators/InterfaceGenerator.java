package compiler.code_generation.generators;

import compiler.code_generation.CodeGeneration;
import compiler.code_generation.constants.CodeGenerationConstant;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;

import java.util.List;

public class InterfaceGenerator {
    public static List<CodeGeneration.ClassFile> generate(InterfaceNode interfaceNode, FileWrapper file) {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String interfaceName = FQNameMapper.getFQName(interfaceNode, file.scope());

        final String[] implementedInterfaces = interfaceNode.implementedInterfaces
            .stream()
            .map(interfaceBase -> FQNameMapper.getFQName(interfaceBase, file.scope()))
            .toArray(String[]::new);

        cw.visit(
            CodeGenerationConstant.byteCodeVersion,
            ModifierMapper.map(interfaceNode.modifiers) | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT,
            interfaceName,
            null,
            CodeGenerationConstant.defaultBaseObjectFQName,
            implementedInterfaces
        );

        final List<CodeGeneration.ClassFile> classes = BlockGenerator.generateClassBlock(interfaceNode.block, file);

        for (final FunctionDeclarationNode functionDeclarationNode : interfaceNode.methods) {
            FunctionGenerator.generate(functionDeclarationNode, file, cw, true);
        }

        cw.visitEnd();

        classes.add(new CodeGeneration.ClassFile(interfaceNode.name + ".class", cw.toByteArray()));
        return classes;
    }
}