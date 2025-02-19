package compiler.code_generation.manager;

import compiler.code_generation.mappers.BoxMapper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class VariableManager {
    private final Map<String, VariableInfo> variables = new HashMap<>();
    private final MethodVisitor mv;
    private int availableCell = 0;

    public VariableManager(MethodVisitor mv) {
        this.mv = mv;
    }

    public void recognizeVariable(String name, String type, boolean isNullable) {
        variables.put(name, new VariableInfo(availableCell, type, isNullable));
    }

    public void declareVariable(String name, String type, boolean isNullable) {
        recognizeVariable(name, type, isNullable);
        storeVariable(name);
    }

    public void loadVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new IllegalArgumentException("Variable '" + name + "' does not exist");
        }
        final VariableInfo variableInfo = variables.get(name);

        if (!variableInfo.isNullable && isPrimitive(variableInfo.type)) {
            BoxMapper.unbox(variableInfo.type, mv);
        }

        mv.visitVarInsn(
            getLoadOpCode(
                variableInfo.type,
                variableInfo.isNullable
            ),
            variableInfo.index
        );
    }

    public void storeVariable(String name) {
        VariableInfo varInfo = variables.get(name);
        if (varInfo == null) {
            throw new IllegalArgumentException("Variable '" + name + "' does not exist");
        }

        int opcode = getStoreOpcode(varInfo.type, varInfo.isNullable);
        mv.visitVarInsn(opcode, varInfo.index);
        availableCell++;
    }

    private int getStoreOpcode(String type, boolean isNullable) {
        if (isNullable) return Opcodes.ASTORE;

        return switch (type) {
            case "Int", "Bool", "Byte", "Short", "Char" -> Opcodes.ISTORE;
            case "Float" -> Opcodes.FSTORE;
            case "Double" -> Opcodes.DSTORE;
            case "Long" -> Opcodes.LSTORE;
            default -> Opcodes.ASTORE;
        };
    }

    private int getLoadOpCode(String type, boolean isNullable) {
        if (isNullable) return Opcodes.ALOAD;

        return switch (type) {
            case "Int", "Bool", "Byte", "Short", "Char" -> Opcodes.ILOAD;
            case "Float" -> Opcodes.FLOAD;
            case "Double" -> Opcodes.DLOAD;
            case "Long" -> Opcodes.LLOAD;
            default -> Opcodes.ALOAD;
        };
    }

    private boolean isPrimitive(String type) {
        return switch (type) {
            case "Int", "Bool", "Float", "Double", "Long", "Byte", "Char", "Short" -> true;
            default -> false;
        };
    }

    private record VariableInfo(int index, String type, boolean isNullable) {}
}
