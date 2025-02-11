package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.FQNameMapper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.classes.*;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.*;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.loaders.SignatureLoader;
import semantic_analysis.scopes.Scope;

import static compiler.code_generation.generators.FunctionGenerator.getJVMName;

public class ExpressionGenerator {
    public static void generate(ExpressionNode expression, MethodVisitor mv, VariableManager vm, FileWrapper file) {
        if (expression instanceof VariableReferenceNode referenceNode) {
            generateVarReference(referenceNode, vm, mv);
        } else if (expression instanceof FunctionCallNode functionCallNode) {
            generateFuncCall(functionCallNode, file, vm, mv);
        } else if (expression instanceof ObjectNode objectNode) {
            generateObjectInstantiation(objectNode, file.scope(), file, vm, mv);
        } else if (expression instanceof FieldReferenceNode fieldReferenceNode) {
            generateFieldReference(fieldReferenceNode, file.scope(), mv);
        } else if (expression instanceof LiteralNode literalNode) {
            generateLiteral(literalNode, mv);
        } else if (expression instanceof NullLiteral) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else {
            throw new UnsupportedOperationException("Unknown expression type: " + expression.getClass().getSimpleName());
        }
    }

    private static void generateVarReference(VariableReferenceNode refNode, VariableManager vm, MethodVisitor mv) {
        mv.visitVarInsn(Opcodes.ALOAD, vm.loadVariable(refNode.variable));
    }

    private static void generateFuncCall(FunctionCallNode funcCallNode, FileWrapper file, VariableManager vm, MethodVisitor mv) {
        System.out.println(funcCallNode.name);
        System.out.println(funcCallNode.arguments);
        final FunctionDeclarationNode declaration = SignatureLoader.findMethodWithParametersInAll(
            file.scope(),
            funcCallNode.name,
            funcCallNode.arguments.stream()
                .map(argument -> argument.type).toList()
        );
        final String descriptor = FunctionGenerator.getDescriptor(declaration.parameters, declaration.returnType, file.scope());
        final boolean isStatic = declaration.modifiers.contains("static");

        for (ArgumentNode arg : funcCallNode.arguments) {
            generate(arg.value.expression, mv, vm, file);
        }

        if (funcCallNode.callerType == null) {
            final String topLevelClassName = file.name() + "Fl";
            final String fqTopLevelName = FQNameMapper.getFQName(topLevelClassName, file.scope());

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, fqTopLevelName, funcCallNode.name, descriptor, false);
        } else {
            final TypeDeclarationNode caller = file.scope().getTypeDeclaration(funcCallNode.callerType);
            boolean isInterface = caller instanceof InterfaceNode;

            final int callType = isInterface ? Opcodes.INVOKEINTERFACE
                : isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
            final String fqCallerName = FQNameMapper.getFQName(funcCallNode.callerType, file.scope());

            mv.visitMethodInsn(callType, fqCallerName, funcCallNode.name, descriptor, isInterface);
        }
    }

    private static void generateFieldReference(FieldReferenceNode refNode, Scope scope, MethodVisitor mv) {
        final String holderFQName = FQNameMapper.getFQName(refNode.holderType, scope);
        final String descriptor = getJVMName(refNode.type.type(), scope);

        mv.visitFieldInsn(Opcodes.GETFIELD, holderFQName, refNode.name, descriptor);
    }

    private static void generateObjectInstantiation(ObjectNode objNode, Scope scope, FileWrapper file, VariableManager vm, MethodVisitor mv) {
        final String fqObjectName = FQNameMapper.getFQName(objNode.className, scope);

        final ClassDeclarationNode objClass = scope.getClass(objNode.className);
        if (objClass == null) {
            throw new RuntimeException("Class " + objNode.className + " was not found");
        }

        ConstructorNode constructorNode = null;
        for (final ConstructorNode currentConstructor : objClass.constructors) {
            if (SignatureLoader.compareParameterTypesWithoutThis(scope, currentConstructor.parameters, objNode.arguments)) {
                constructorNode = currentConstructor;
            }
        }

        if (constructorNode == null) {
            throw new RuntimeException("Constructor " + objNode.className + " was not found");
        }

        final String constructorDescriptor = FunctionGenerator.getDescriptor(constructorNode.parameters, "Void", scope);

        mv.visitTypeInsn(Opcodes.NEW, fqObjectName);
        mv.visitInsn(Opcodes.DUP);

        for (ArgumentNode arg : objNode.arguments) {
            generate(arg.value.expression, mv, vm, file);
        }

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, fqObjectName, "<init>", constructorDescriptor, false);
    }

    public static void generateLiteral(LiteralNode literalNode, MethodVisitor mv) {
        mv.visitLdcInsn(literalNode.getValue());
    }
}
