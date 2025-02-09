package compiler.code_generation.generators;

import compiler.code_generation.constants.CodeGenerationConstant;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.InterfaceNode;
import semantic_analysis.scopes.Scope;

public class InterfaceGenerator {
    public static ClassWriter generate(InterfaceNode interfaceNode, Scope scope) {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String interfaceName = FQNameMapper.getFQName(interfaceNode, scope);

        final String[] implementedInterfaces = interfaceNode.implementedInterfaces
            .stream()
            .map(interfaceBase -> FQNameMapper.getFQName(interfaceBase, scope))
            .toArray(String[]::new);

        cw.visit(
            CodeGenerationConstant.byteCodeVersion,
            ModifierMapper.map(interfaceNode.modifiers) | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT,
            interfaceName,
            null,
            "java/lang/Object",
            implementedInterfaces
        );

        // TODO: Generate ABSTRACT methods, and block

        return cw;
    }
}