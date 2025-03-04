package compiler.code_generation.mappers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;

public class BoxMapper {
    public static void boxIfNeeded(FlowType type, FlowType expectedType, MethodVisitor mv) {
        if (expectedType != null) {
            if (expectedType.name.startsWith("java.")) {
                mapToJava(expectedType, mv);
                return;
            } else if (type.name.startsWith("java.")) {
                mapFromJava(type, mv);
                return;
            }

            if (expectedType.shouldBePrimitive()) {
                expectedType.isPrimitive = true;
            }

            if (needUnboxing(type, expectedType)) {
                unbox(type, mv);
            } else if (needBoxing(type, expectedType)) {
                box(type, mv);
            }
        }
    }

    public static boolean needBoxing(FlowType type, FlowType expectedType) {
        return (type.isPrimitive && (expectedType.isNullable || !expectedType.isPrimitive)) && type.isPrimitiveType();
    }

    public static boolean needUnboxing(FlowType type, FlowType expectedType) {
        return expectedType.isPrimitive && type.shouldBePrimitive();
    }

    public static void box(FlowType type, MethodVisitor mv) {
        switch (type.name) {
            case "Int" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Int", "fromPrimitive", "(I)Lflow/Int;", false);
            case "Bool" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Bool", "fromPrimitive", "(Z)Lflow/Bool;", false);
            case "Float" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Float", "fromPrimitive", "(F)Lflow/Float;", false);
            case "Double" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Double", "fromPrimitive", "(D)Lflow/Double;", false);
            case "Long" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Long", "fromPrimitive", "(J)Lflow/Long;", false);
            case "Byte" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Byte", "fromPrimitive", "(B)Lflow/Byte;", false);
            case "Char" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Char", "fromPrimitive", "(C)Lflow/Char;", false);
            case "Short" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Short", "fromPrimitive", "(S)Lflow/Short;", false);
            default -> throw new IllegalArgumentException("Cannot box unknown type: " + type);
        }
    }

    public static void unbox(FlowType type, MethodVisitor mv) {
        switch (type.name) {
            case "Int" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Int", "value", "I");
            case "Bool" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Bool", "value", "Z");
            case "Float" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Float", "value", "F");
            case "Double" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Double", "value", "D");
            case "Long" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Long", "value", "J");
            case "Byte" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Byte", "value", "B");
            case "Char" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Char", "value", "C");
            case "Short" -> mv.visitFieldInsn(Opcodes.GETFIELD, "flow/Short", "value", "S");
        }
    }

    public static void mapToJava(FlowType type, MethodVisitor mv) {
        switch (type.name) {
            case "Int" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Int", "toJavaType", "()Ljava/lang/Integer;", false);
            case "Bool" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Bool", "toJavaType", "()Ljava/lang/Boolean;", false);
            case "Float" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Float", "toJavaType", "()Ljava/lang/Float;", false);
            case "Double" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Double", "toJavaType", "()Ljava/lang/Double;", false);
            case "Long" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Long", "toJavaType", "()Ljava/lang/Long;", false);
            case "Byte" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Byte", "toJavaType", "()Ljava/lang/Byte;", false);
            case "Char" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Char", "toJavaType", "()Ljava/lang/Character;", false);
            case "Short" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/Short", "toJavaType", "()Ljava/lang/Short;", false);
            case "String" -> mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "flow/String", "toJavaType", "()Ljava/lang/String;", false);
        }
    }

    public static void mapFromJava(FlowType expectedType, MethodVisitor mv) {
        switch (expectedType.name) {
            case "Int" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Int", "fromJavaType", "(Ljava/lang/Integer;)Lflow/Int;", false);
            case "Bool" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Bool", "fromJavaType", "(Ljava/lang/Boolean;)Lflow/Bool;", false);
            case "Float" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Float", "fromJavaType", "(Ljava/lang/Float;)Lflow/Float;", false);
            case "Double" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Double", "fromJavaType", "(Ljava/lang/Double;)Lflow/Double;", false);
            case "Long" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Long", "fromJavaType", "(Ljava/lang/Long;)Lflow/Long;", false);
            case "Byte" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Byte", "fromJavaType", "(Ljava/lang/Byte;)Lflow/Byte;", false);
            case "Char" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Char", "fromJavaType", "(Ljava/lang/Character;)Lflow/Char;", false);
            case "Short" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/Short", "fromJavaType", "(Ljava/lang/Short;)Lflow/Short;", false);
            case "String" -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "flow/String", "fromJavaType", "(Ljava/lang/String;)Lflow/String;", false);
        }
    }
}
