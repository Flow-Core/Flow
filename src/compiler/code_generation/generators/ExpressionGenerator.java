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
        for (ArgumentNode arg : funcCallNode.arguments) {
            generate(arg.value.expression, mv, vm, file);
        }

        if (funcCallNode.callerType == null) {
            final String topLevelClassName = file.name() + "Fl";
            final String fqTopLevelName = FQNameMapper.getFQName(topLevelClassName, file.scope());

            final FunctionDeclarationNode declaration = SignatureLoader.findMethodWithParameters(
                file.scope(),
                funcCallNode.name,
                funcCallNode.arguments.stream()
                    .map(argument -> argument.type).toList()
            );
            final String descriptor = FunctionGenerator.getDescriptor(declaration, file.scope());

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, fqTopLevelName, funcCallNode.name, descriptor, false);
        } else {
            final TypeDeclarationNode caller = file.scope().getTypeDeclaration(funcCallNode.callerType);
            if (caller == null) {
                throw new RuntimeException("Caller not found in scope: " + funcCallNode.callerType);
            }

            boolean isInterface = caller instanceof InterfaceNode;

            final FunctionDeclarationNode declaration = SignatureLoader.findMethodWithParameters(
                file.scope(),
                caller.methods,
                funcCallNode.name,
                funcCallNode.arguments.stream()
                    .map(argument -> argument.type).toList()
            );
            final String descriptor = FunctionGenerator.getDescriptor(declaration, file.scope());
            final boolean isStatic = declaration.modifiers.contains("static");

            final int callType = isInterface ? Opcodes.INVOKEINTERFACE
                : isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
            final String fqCallerName = FQNameMapper.getFQName(funcCallNode.callerType, file.scope());

            mv.visitMethodInsn(callType, fqCallerName, funcCallNode.name, descriptor, isInterface);
        }
    }

    private static void generateFieldReference(FieldReferenceNode refNode, Scope scope, MethodVisitor mv) {
        final String holderFQName = FQNameMapper.getFQName(refNode.holderType, scope);
        final String descriptor = getJVMName(refNode.type.type(), refNode.type.isNullable(), scope);

        if (refNode.isStatic)
            mv.visitFieldInsn(Opcodes.GETSTATIC, holderFQName, refNode.name, descriptor);
        else
            mv.visitFieldInsn(Opcodes.GETFIELD, holderFQName, refNode.name, descriptor);
    }

    private static void generateObjectInstantiation(ObjectNode objNode, Scope scope, FileWrapper file, VariableManager vm, MethodVisitor mv) {
        if (objNode.className.equals("Void")) {
            return;
        }

        final String fqObjectName = FQNameMapper.getFQName(objNode.className, scope);

        final ClassDeclarationNode objClass = scope.getClass(objNode.className);
        if (objClass == null) {
            throw new RuntimeException("Class " + objNode.className + " was not found");
        }

        ConstructorNode constructorNode = null;
        for (final ConstructorNode currentConstructor : objClass.constructors) {
            if (SignatureLoader.compareParameterTypes(scope, currentConstructor.parameters, objNode.arguments)) {
                constructorNode = currentConstructor;
            }
        }

        if (constructorNode == null) {
            throw new RuntimeException("Constructor " + objNode.className + " was not found");
        }

        final String constructorDescriptor = FunctionGenerator.getDescriptor(constructorNode.parameters, "Void", false, scope);

        mv.visitTypeInsn(Opcodes.NEW, fqObjectName);
        mv.visitInsn(Opcodes.DUP);

        for (ArgumentNode arg : objNode.arguments) {
            generate(arg.value.expression, mv, vm, file);
        }

        // TODO: figure out why
        if (objNode.className.equals("Bool")) {
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "flow/Bool", "<init>", "(I)V", false);
            return;
        }
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, fqObjectName, "<init>", constructorDescriptor, false);
    }

    public static void generateLiteral(LiteralNode literalNode, MethodVisitor mv) {
        if (literalNode instanceof VoidLiteralNode) {
            return;
        } else if (literalNode instanceof BooleanLiteralNode booleanLiteralNode) {
            mv.visitInsn(booleanLiteralNode.value ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
            return;
        } else if (literalNode instanceof IntegerLiteralNode intLiteralNode) {
            int value = intLiteralNode.value;
            if (value >= -1 && value <= 5) {
                mv.visitInsn(Opcodes.ICONST_0 + value);
            } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.BIPUSH, value);
            } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.SIPUSH, value);
            } else {
                mv.visitLdcInsn(value);
            }
            return;
        } else if (literalNode instanceof FloatLiteralNode floatLiteralNode) {
            float value = floatLiteralNode.value;
            if (value == 0.0f) {
                mv.visitInsn(Opcodes.FCONST_0);
            } else if (value == 1.0f) {
                mv.visitInsn(Opcodes.FCONST_1);
            } else if (value == 2.0f) {
                mv.visitInsn(Opcodes.FCONST_2);
            } else {
                mv.visitLdcInsn(value);
            }
            return;
        } else if (literalNode instanceof DoubleLiteralNode doubleLiteralNode) {
            double value = doubleLiteralNode.value;
            if (value == 0.0) {
                mv.visitInsn(Opcodes.DCONST_0);
            } else if (value == 1.0) {
                mv.visitInsn(Opcodes.DCONST_1);
            } else {
                mv.visitLdcInsn(value);
            }
            return;
        } else if (literalNode instanceof LongLiteralNode longLiteralNode) {
            long value = longLiteralNode.value;
            if (value == 0L) {
                mv.visitInsn(Opcodes.LCONST_0);
            } else if (value == 1L) {
                mv.visitInsn(Opcodes.LCONST_1);
            } else {
                mv.visitLdcInsn(value);
            }
            return;
        } else if (literalNode instanceof CharLiteralNode charLiteralNode) {
            char value = charLiteralNode.value;
            if (value <= 5) {
                mv.visitInsn(Opcodes.ICONST_0 + value);
            } else if (value <= Byte.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.BIPUSH, value);
            } else {
                mv.visitLdcInsn((int) value);
            }
            return;
        }

        mv.visitLdcInsn(literalNode.getValue());
    }

    private static void boxPrimitive(String type, MethodVisitor mv, Scope scope) {
        String wrapperType = getBoxedType(type);
        String descriptor = "(" + getJVMName(type, false, scope) + ")L" + wrapperType + ";";
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, wrapperType, "valueOf", descriptor, false);
    }

    private static boolean isPrimitive(String type) {
        return switch (type) {
            case "Int", "Bool", "Float", "Double", "Long", "Byte", "Char", "Short" -> true;
            default -> false;
        };
    }

    private static String getBoxedType(String primitive) {
        return switch (primitive) {
            case "Int" -> "java/lang/Integer";
            case "Bool" -> "java/lang/Boolean";
            case "Float" -> "java/lang/Float";
            case "Double" -> "java/lang/Double";
            case "Long" -> "java/lang/Long";
            case "Byte" -> "java/lang/Byte";
            case "Char" -> "java/lang/Character";
            case "Short" -> "java/lang/Short";
            default -> throw new IllegalArgumentException("Unexpected primitive: " + primitive);
        };
    }
}
