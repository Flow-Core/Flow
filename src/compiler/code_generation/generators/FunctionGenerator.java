package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.scopes.Scope;

import java.util.List;

public class FunctionGenerator {
    public static void generate(FunctionDeclarationNode functionDeclarationNode, FileWrapper file, ClassWriter classWriter, boolean isSignature) {
        boolean isAbstract = functionDeclarationNode.block == null && isSignature;

        final MethodVisitor mv = classWriter.visitMethod(
            ModifierMapper.map(functionDeclarationNode.modifiers) | (isAbstract ? (Opcodes.ACC_ABSTRACT | Opcodes.ACC_PUBLIC) : 0),
            functionDeclarationNode.name,
            getDescriptor(functionDeclarationNode.parameters, functionDeclarationNode.returnType, file.scope()),
            null,
            null
        );

        if (!isAbstract) {
            BlockGenerator.generateFunctionBlock(functionDeclarationNode.block, file, mv, new VariableManager(mv));
        }

        mv.visitEnd();
    }

    public static String getDescriptor(List<ParameterNode> parameters, String returnType, Scope scope) {
        final StringBuilder sb = new StringBuilder("(");

        for (final ParameterNode parameterNode : parameters) {
            sb.append(getJVMName(parameterNode.type, scope));
        }

        sb.append(")").append(getJVMName(returnType, scope));

        return sb.toString();
    }

    public static String getJVMName(String type, Scope scope) {
        if (type.equals("Void")) {
            return "V";
        }

        return "L" + FQNameMapper.getFQName(type, scope) + ";";
    }
}
