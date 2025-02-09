package compiler.code_generation.generators;

import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.scopes.Scope;

public class FunctionGenerator {
    public static MethodVisitor generate(FunctionDeclarationNode functionDeclarationNode, Scope scope, ClassWriter classWriter, boolean isSignature) {
        final MethodVisitor mv = classWriter.visitMethod(
            ModifierMapper.map(functionDeclarationNode.modifiers) | (isSignature ? (Opcodes.ACC_ABSTRACT | Opcodes.ACC_PUBLIC) : 0),
            functionDeclarationNode.name,
            getDescriptor(functionDeclarationNode, scope),
            null,
            null
        );

        if (!isSignature) {
            // TODO: generate body
        }

        mv.visitEnd();

        return mv;
    }

    private static String getDescriptor(FunctionDeclarationNode functionDeclarationNode, Scope scope) {
        final StringBuilder sb = new StringBuilder("(");

        for (final ParameterNode parameterNode : functionDeclarationNode.parameters) {
            sb.append(getJVMName(parameterNode.type, scope));
        }

        sb.append(")").append(getJVMName(functionDeclarationNode.returnType, scope));

        return sb.toString();
    }

    private static String getJVMName(String type, Scope scope) {
        if (type.equals("Void")) {
            return "V";
        }

        return "L" + FQNameMapper.getFQName(type, scope) + ";";
    }
}
