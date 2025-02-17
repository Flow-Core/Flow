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
    public static void generate(FunctionDeclarationNode functionDeclarationNode, FileWrapper file, ClassWriter cw, boolean isSignature) {
        boolean isAbstract = functionDeclarationNode.block == null && isSignature;

        final MethodVisitor mv = cw.visitMethod(
            ModifierMapper.map(functionDeclarationNode.modifiers) | (isAbstract ? (Opcodes.ACC_ABSTRACT | Opcodes.ACC_PUBLIC) : 0),
            functionDeclarationNode.name,
            getDescriptor(functionDeclarationNode, file.scope()),
            null,
            null
        );

        if (!isAbstract) {
            mv.visitCode();

            VariableManager vm =  new VariableManager(mv);

            for (ParameterNode parameterNode : functionDeclarationNode.parameters) {
                vm.recognizeVariable(parameterNode.name, parameterNode.type, parameterNode.isNullable);
            }

            BlockGenerator.generateFunctionBlock(functionDeclarationNode.block, file, mv, vm);

            if (functionDeclarationNode.returnType.equals("Void")) {
                mv.visitInsn(Opcodes.RETURN);
            }

            mv.visitMaxs(0, 0);
        }

        mv.visitEnd();
    }

    public static String getDescriptor(
        List<ParameterNode> parameters,
        String returnType,
        boolean isReturnTypeNullable,
        Scope scope
    ) {
        final StringBuilder sb = new StringBuilder("(");

        for (final ParameterNode parameterNode : parameters) {
            sb.append(getJVMName(parameterNode.type, parameterNode.isNullable, scope));
        }

        sb.append(")").append(getJVMName(returnType, isReturnTypeNullable, scope));

        return sb.toString();
    }

    public static String getDescriptor(FunctionDeclarationNode functionDeclarationNode, Scope scope) {
        final StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < functionDeclarationNode.parameters.size(); i++) {
            final ParameterNode parameterNode = functionDeclarationNode.parameters.get(i);

            if (i > 0 || functionDeclarationNode.modifiers.contains("static")) {
                sb.append(getJVMName(parameterNode.type, parameterNode.isNullable, scope));
            }
        }

        sb.append(")").append(getJVMName(functionDeclarationNode.returnType, functionDeclarationNode.isReturnTypeNullable, scope));

        return sb.toString();
    }

    public static String getJVMName(String type, boolean isNullable, Scope scope) {
        if (isNullable) {
            return switch (type) {
                case "Int" -> "Ljava/lang/Integer;";
                case "Bool" -> "Ljava/lang/Boolean;";
                case "Float" -> "Ljava/lang/Float;";
                case "Double" -> "Ljava/lang/Double;";
                case "Long" -> "Ljava/lang/Long;";
                case "Byte" -> "Ljava/lang/Byte;";
                case "Char" -> "Ljava/lang/Character;";
                case "Short" -> "Ljava/lang/Short;";
                case "Void" -> "Ljava/lang/Void;";
                default -> "L" + FQNameMapper.getFQName(type, scope) + ";";
            };
        }

        return switch (type) {
            case "Void" -> "V";
            case "Int" -> "I";
            case "Bool" -> "Z";
            case "Float" -> "F";
            case "Double" -> "D";
            case "Long" -> "J";
            case "Byte" -> "B";
            case "Char" -> "C";
            case "Short" -> "S";
            default -> "L" + FQNameMapper.getFQName(type, scope) + ";";
        };
    }
}
