package compiler.code_generation.mappers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BoxMapper {
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

    public static String getPrimitiveDescriptor(String type) {
        return switch (type) {
            case "Int" -> "I";
            case "Bool" -> "Z";
            case "Float" -> "F";
            case "Double" -> "D";
            case "Long" -> "J";
            case "Byte" -> "B";
            case "Char" -> "C";
            case "Short" -> "S";
            default -> null;
        };
    }

    private static String getBoxedType(String type) {
        return switch (type) {
            case "Int" -> "flow/Int";
            case "Bool" -> "flow/Bool";
            case "Float" -> "flow/Float";
            case "Double" -> "flow/Double";
            case "Long" -> "flow/Long";
            case "Byte" -> "flow/Byte";
            case "Char" -> "flow/Char";
            case "Short" -> "flow/Short";
            default -> type;
        };
    }
}
