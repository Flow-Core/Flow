package compiler.code_generation.generators;

import compiler.code_generation.constants.CodeGenerationConstant;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import parser.nodes.classes.ClassDeclarationNode;
import semantic_analysis.scopes.Scope;

public class ClassGenerator {
    public static ClassWriter generate(ClassDeclarationNode classDeclarationNode, Scope scope) {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String baseClassName = CodeGenerationConstant.baseObjectFQName;
        if (!classDeclarationNode.baseClasses.isEmpty()) {
            baseClassName = scope.getFQName(classDeclarationNode.baseClasses.get(0));
            if (baseClassName == null) {
                throw new IllegalArgumentException("Class should be loaded in the binding context");
            }

            baseClassName = FQNameMapper.map(baseClassName);
        }

        final String[] baseInterfaceNames = (String[]) classDeclarationNode.implementedInterfaces
            .stream().map(baseInterfaceNode -> FQNameMapper.map(baseInterfaceNode.name)).toArray();

        cw.visit(
            CodeGenerationConstant.byteCodeVersion,
            ModifierMapper.map(classDeclarationNode.modifiers),
            classDeclarationNode.name,
            null,
            baseClassName,
            baseInterfaceNames
        );

        // TODO: generate methods, ctors, fields, block
        return cw;
    }
}
