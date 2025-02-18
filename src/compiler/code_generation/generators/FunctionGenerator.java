package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.BoxMapper;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;
import parser.nodes.classes.BaseClassNode;
import parser.nodes.classes.ObjectNode;
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
                vm.recognizeVariable(parameterNode.name, parameterNode.type);
            }

            BlockGenerator.generateFunctionBlock(functionDeclarationNode.block.scope, functionDeclarationNode.block.blockNode, file, mv, vm);

            if (functionDeclarationNode.returnType.name.equals("Void")) {
                mv.visitInsn(Opcodes.RETURN);
            }

            mv.visitMaxs(0, 0);
        }

        mv.visitEnd();
    }

    public static void generateConstructor(BaseClassNode baseClassNode, FunctionDeclarationNode functionDeclarationNode, FileWrapper file, ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod(
            ModifierMapper.map(functionDeclarationNode.modifiers),
            functionDeclarationNode.name,
            getDescriptor(
                functionDeclarationNode.parameters,
                functionDeclarationNode.returnType,
                file.scope()
            ),
            null,
            null
        );

        mv.visitCode();

        VariableManager vm =  new VariableManager(mv);

        for (ParameterNode parameterNode : functionDeclarationNode.parameters) {
            vm.recognizeVariable(parameterNode.name, parameterNode.type);
        }

        mv.visitVarInsn(Opcodes.ALOAD, 0);

        if (baseClassNode != null) {
            ExpressionGenerator.generateConstructorCall(
                new ObjectNode(
                    baseClassNode.name,
                    baseClassNode.arguments
                ),
                file.scope(),
                file,
                vm,
                mv
            );
        } else {
            ExpressionGenerator.generateConstructorCall(
                new ObjectNode(
                    "flow.Thing",
                    List.of()
                ),
                file.scope(),
                file,
                vm,
                mv
            );
        }

        BlockGenerator.generateFunctionBlock(functionDeclarationNode.block.scope, functionDeclarationNode.block.blockNode, file, mv, vm);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);

        mv.visitEnd();
    }

    public static String getDescriptor(
        List<ParameterNode> parameters,
        FlowType returnType,
        Scope scope
    ) {
        final StringBuilder sb = new StringBuilder("(");

        for (final ParameterNode parameterNode : parameters) {
            sb.append(getJVMName(parameterNode.type, scope));
        }

        sb.append(")").append(getJVMName(returnType, scope));

        return sb.toString();
    }

    public static String getDescriptor(
        FunctionDeclarationNode functionDeclarationNode,
        Scope scope
    ) {
        final StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < functionDeclarationNode.parameters.size(); i++) {
            final ParameterNode parameterNode = functionDeclarationNode.parameters.get(i);

            if (i > 0 || functionDeclarationNode.modifiers.contains("static")) {
                sb.append(getJVMName(parameterNode.type, scope));
            }
        }

        sb.append(")").append(
            getJVMName(
                functionDeclarationNode.returnType,
                scope
            )
        );

        return sb.toString();
    }

    public static String getJVMName(FlowType type, Scope scope) {
        if (!BoxMapper.needUnboxing(type) && !type.name.equals("Void")) {
            return "L" + FQNameMapper.getFQName(type.name, scope) + ";";
        }

        return switch (type.name) {
            case "Void" -> "V";
            case "Int" -> "I";
            case "Bool" -> "Z";
            case "Float" -> "F";
            case "Double" -> "D";
            case "Long" -> "J";
            case "Byte" -> "B";
            case "Char" -> "C";
            case "Short" -> "S";
            default -> "L" + FQNameMapper.getFQName(type.name, scope) + ";";
        };
    }
}