package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.ObjectNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.scopes.Scope;

import static compiler.code_generation.generators.FunctionGenerator.getJVMName;
import static semantic_analysis.loaders.SignatureLoader.findMethodWithParameters;

public class ExpressionGenerator {
    private static void generateVarReference(VariableReferenceNode refNode, VariableManager vm, MethodVisitor mv) {
        mv.visitVarInsn(Opcodes.ALOAD, vm.loadVariable(refNode.variable));
    }

    private static void generateFuncCall(FunctionCallNode funcCallNode, Scope scope, VariableManager vm, MethodVisitor mv) {
        if (funcCallNode.callerType == null) {
            final FunctionDeclarationNode declaration = findMethodWithParameters(
                scope,
                funcCallNode.name,
                funcCallNode.arguments.stream()
                    .map(argument -> argument.type).toList()
            );

            final String descriptor = FunctionGenerator.getDescriptor(declaration, scope);
        }
    }

    private static void generateFieldReference(FieldReferenceNode refNode, Scope scope, VariableManager vm, MethodVisitor mv) {
        final String holderFQName = FQNameMapper.getFQName(refNode.holderType, scope);
        final String descriptor = getJVMName(refNode.type.type(), scope);

        mv.visitFieldInsn(Opcodes.GETFIELD, holderFQName, refNode.name, descriptor);
    }

    private static void generateObjectInstantiation(ObjectNode objNode, Scope scope, VariableManager vm, MethodVisitor mv) {

    }
}
