package compiler.code_generation.generators;

import compiler.code_generation.constants.CodeGenerationConstant;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;

import java.util.List;

public class ClassGenerator {
    public static List<ClassWriter> generate(ClassDeclarationNode classDeclarationNode, FileWrapper file) {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String baseClassName = CodeGenerationConstant.baseObjectFQName;
        if (!classDeclarationNode.baseClasses.isEmpty()) {
            baseClassName = FQNameMapper.getFQName(classDeclarationNode.baseClasses.get(0), file.scope());
        }

        final String[] baseInterfaceNames = classDeclarationNode.implementedInterfaces
            .stream().map(baseInterfaceNode -> FQNameMapper.getFQName(baseInterfaceNode, file.scope()))
            .toArray(String[]::new);

        cw.visit(
            CodeGenerationConstant.byteCodeVersion,
            ModifierMapper.map(classDeclarationNode.modifiers),
            FQNameMapper.getFQName(classDeclarationNode, file.scope()),
            null,
            baseClassName,
            baseInterfaceNames
        );

        final List<ClassWriter> classes = BlockGenerator.generateClassBlock(classDeclarationNode.classBlock, file);
        classes.add(cw);

        for (final FunctionDeclarationNode functionDeclarationNode : classDeclarationNode.methods) {
            FunctionGenerator.generate(functionDeclarationNode, file, cw, false);
        }

        cw.visitEnd();

        // TODO: generate methods, ctors, fields
        return classes;
    }
}
