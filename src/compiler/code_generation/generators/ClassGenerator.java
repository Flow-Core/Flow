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
            baseClassName = FQNameMapper.getFQName(classDeclarationNode.baseClasses.get(0), scope);
        }

        final String[] baseInterfaceNames = classDeclarationNode.implementedInterfaces
            .stream().map(baseInterfaceNode -> FQNameMapper.getFQName(baseInterfaceNode, scope))
            .toArray(String[]::new);

        cw.visit(
            CodeGenerationConstant.byteCodeVersion,
            ModifierMapper.map(classDeclarationNode.modifiers),
            FQNameMapper.getFQName(classDeclarationNode, scope),
            null,
            baseClassName,
            baseInterfaceNames
        );

        // TODO: generate methods, ctors, fields, block
        return cw;
    }
}
