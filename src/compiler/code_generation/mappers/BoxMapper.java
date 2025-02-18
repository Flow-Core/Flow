package compiler.code_generation.mappers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BoxMapper {
    public static void box(String type, MethodVisitor mv) {
        switch (type) {
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

    public static void unbox(String type, MethodVisitor mv) {
        switch (type) {
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
}
