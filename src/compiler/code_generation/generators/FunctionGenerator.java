package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;
import parser.nodes.classes.BaseClassNode;
import parser.nodes.classes.ObjectNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.scopes.Scope;

import java.util.List;

public class FunctionGenerator {
    public static void generate(
        FunctionDeclarationNode functionDeclarationNode,
        TypeDeclarationNode containingType,
        FileWrapper file,
        ClassWriter cw,
        boolean isSignature
    ) {
        boolean isAbstract = functionDeclarationNode.body == null && isSignature;

        final MethodVisitor mv = cw.visitMethod(
            ModifierMapper.map(functionDeclarationNode.modifiers) | (isAbstract ? (Opcodes.ACC_ABSTRACT | Opcodes.ACC_PUBLIC) : 0),
            functionDeclarationNode.name,
            getDescriptor(functionDeclarationNode, file.scope(), containingType.typeParameters),
            getFunctionSignature(functionDeclarationNode, file.scope(), containingType.typeParameters),
            null
        );

        if (!isAbstract) {
            mv.visitCode();

            VariableManager vm =  new VariableManager(mv);

            if (!functionDeclarationNode.modifiers.contains("static")) {
                vm.recognizeVariable(
                    "this",
                    new FlowType(
                        containingType.name,
                        false,
                        false
                    )
                );
            }

            for (ParameterNode parameterNode : functionDeclarationNode.parameters) {
                vm.recognizeVariable(parameterNode.name, parameterNode.type);
            }

            BlockGenerator.generateFunctionBlock(functionDeclarationNode.body.scope, functionDeclarationNode.body.blockNode, file, mv, vm);

            if (functionDeclarationNode.returnType.name.equals("Void")) {
                mv.visitInsn(Opcodes.RETURN);
            }

            mv.visitMaxs(0, 0);
        }

        mv.visitEnd();
    }

    public static void generateConstructor(
        BaseClassNode baseClassNode,
        FunctionDeclarationNode functionDeclarationNode,
        TypeDeclarationNode containingType,
        FileWrapper file,
        ClassWriter cw
    ) {
        final MethodVisitor mv = cw.visitMethod(
            ModifierMapper.map(functionDeclarationNode.modifiers),
            functionDeclarationNode.name,
            getDescriptor(
                functionDeclarationNode.parameters,
                functionDeclarationNode.returnType,
                file.scope(),
                containingType.typeParameters
            ),
            null,
            null
        );

        mv.visitCode();

        VariableManager vm =  new VariableManager(mv);
        vm.recognizeVariable(
            "this",
            new FlowType(
                containingType.name,
                false,
                false
            )
        );

        for (ParameterNode parameterNode : functionDeclarationNode.parameters) {
            if (parameterNode.type.shouldBePrimitive)
                parameterNode.type.isPrimitive = true;

            vm.recognizeVariable(parameterNode.name, parameterNode.type);
        }

        mv.visitVarInsn(Opcodes.ALOAD, 0);

        if (baseClassNode != null) {
            ExpressionGenerator.generateConstructorCall(
                new ObjectNode(
                    baseClassNode.type,
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
                    new FlowType("flow.Thing", false, false),
                    List.of()
                ),
                file.scope(),
                file,
                vm,
                mv
            );
        }

        BlockGenerator.generateFunctionBlock(functionDeclarationNode.body.scope, functionDeclarationNode.body.blockNode, file, mv, vm);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);

        mv.visitEnd();
    }

    public static String getDescriptor(
        List<ParameterNode> parameters,
        FlowType returnType,
        Scope scope,
        List<TypeParameterNode> typeParameters
    ) {
        final StringBuilder sb = new StringBuilder("(");

        for (final ParameterNode parameterNode : parameters) {
            sb.append(FQNameMapper.getJVMName(parameterNode.type, scope, typeParameters));
        }

        sb.append(")").append(FQNameMapper.getJVMName(returnType, scope, typeParameters));

        return sb.toString();
    }

    public static String getDescriptor(
        FunctionDeclarationNode functionDeclarationNode,
        Scope scope,
        List<TypeParameterNode> typeParameters
    ) {
        final StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < functionDeclarationNode.parameters.size(); i++) {
            final ParameterNode parameterNode = functionDeclarationNode.parameters.get(i);

            sb.append(FQNameMapper.getJVMName(parameterNode.type, scope, typeParameters));
        }

        sb.append(")").append(
            FQNameMapper.getJVMName(
                functionDeclarationNode.returnType,
                scope,
                typeParameters
            )
        );

        return sb.toString();
    }

    public static String getFunctionSignature(FunctionDeclarationNode function, Scope scope, List<TypeParameterNode> typeParameters) {
        StringBuilder signature = new StringBuilder();

        String typeParametersSignature = FQNameMapper.parseTypeParameterSignature(function.typeParameters, scope);
        if (typeParametersSignature != null) {
            signature.append(typeParametersSignature);
        }

        signature.append("(");
        for (ParameterNode parameter : function.parameters) {
            signature.append(FQNameMapper.getJVMName(parameter.type, scope, typeParameters, true));
        }
        signature.append(")");

        signature.append(FQNameMapper.getJVMName(function.returnType, scope, typeParameters, true));

        return signature.toString();
    }

}