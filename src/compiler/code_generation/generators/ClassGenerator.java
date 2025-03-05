package compiler.code_generation.generators;

import compiler.code_generation.CodeGeneration;
import compiler.code_generation.constants.CodeGenerationConstant;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import parser.nodes.FlowType;
import parser.nodes.classes.BaseClassNode;
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
        BaseClassNode baseClassNode = null;
        if (!classDeclarationNode.baseClasses.isEmpty()) {
            baseClassNode = classDeclarationNode.baseClasses.get(0);
            baseClassName = FQNameMapper.getFQName(baseClassNode, file.scope());
        }

        final String[] baseInterfaceNames = classDeclarationNode.implementedInterfaces
            .stream().map(baseInterfaceNode -> FQNameMapper.getFQName(baseInterfaceNode, file.scope()))
            .toArray(String[]::new);

        cw.visit(
            CodeGenerationConstant.byteCodeVersion,
            ModifierMapper.map(classDeclarationNode.modifiers),
            FQNameMapper.getFQName(classDeclarationNode, file.scope()),
            FQNameMapper.parseTypeParameterSignature(classDeclarationNode.typeParameters, file.scope()),
            baseClassName,
            baseInterfaceNames
        );

        final List<CodeGeneration.ClassFile> classes = BlockGenerator.generateClassBlock(classDeclarationNode.classBlock, file);

        for (final FunctionDeclarationNode functionDeclarationNode : classDeclarationNode.methods) {
            FunctionGenerator.generate(functionDeclarationNode, classDeclarationNode, file, cw, functionDeclarationNode.modifiers.contains("abstract"));
        }

        for (final FieldNode fieldNode : classDeclarationNode.fields) {
            VariableDeclarationGenerator.generateField(fieldNode, cw, file);
        }

        for (final ConstructorNode constructorNode : classDeclarationNode.constructors) {
            final FunctionDeclarationNode function = new FunctionDeclarationNode(
                "<init>",
                new FlowType("Void", false, true),
                List.of(constructorNode.accessModifier),
                constructorNode.parameters,
                List.of(),
                constructorNode.body
            );

            if (classDeclarationNode.initBlock != null)
                constructorNode.body.blockNode.children.addAll(classDeclarationNode.initBlock.children);

            FunctionGenerator.generateConstructor(baseClassNode, function, classDeclarationNode, file, cw);
        }

        if (classDeclarationNode.name.equals(file.name() + "Fl")) {
            MainGenerator.generate(cw, classDeclarationNode, file);
        }

        cw.visitEnd();

        classes.add(new CodeGeneration.ClassFile(classDeclarationNode.name + ".class", cw.toByteArray()));
        return classes;
    }
}
