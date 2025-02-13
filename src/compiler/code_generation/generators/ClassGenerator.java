package compiler.code_generation.generators;

import compiler.code_generation.CodeGeneration;
import compiler.code_generation.constants.CodeGenerationConstant;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;

import java.util.List;

public class ClassGenerator {
    public static List<CodeGeneration.ClassFile> generate(ClassDeclarationNode classDeclarationNode, FileWrapper file) {
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

        final List<CodeGeneration.ClassFile> classes = BlockGenerator.generateClassBlock(classDeclarationNode.classBlock, file);

        for (final FunctionDeclarationNode functionDeclarationNode : classDeclarationNode.methods) {
            FunctionGenerator.generate(functionDeclarationNode, file, cw, false);
        }

        for (final FieldNode fieldNode : classDeclarationNode.fields) {
            VariableDeclarationGenerator.generateField(fieldNode, cw, file);
        }

        for (final ConstructorNode constructorNode : classDeclarationNode.constructors) {
            final FunctionDeclarationNode function = new FunctionDeclarationNode(
                "<init>",
                "Void",
                false,
                List.of(constructorNode.accessModifier),
                constructorNode.parameters,
                constructorNode.body
            );

            FunctionGenerator.generate(function, file, cw, false);
        }

        if (classDeclarationNode.name.equals(file.name() + "Fl")) {
            MainGenerator.generate(cw, classDeclarationNode, file);
        }

        cw.visitEnd();

        classes.add(new CodeGeneration.ClassFile(classDeclarationNode.name + ".class", cw.toByteArray()));
        return classes;
    }
}
