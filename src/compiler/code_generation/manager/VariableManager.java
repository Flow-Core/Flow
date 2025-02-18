package compiler.code_generation.manager;

import compiler.code_generation.mappers.BoxMapper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;

import java.util.HashMap;
import java.util.Map;

public class VariableManager {
    private final Map<String, VariableInfo> variables = new HashMap<>();
    private final MethodVisitor mv;
    private int availableCell = 0;

    public VariableManager(MethodVisitor mv) {
        this.mv = mv;
    }

    public void recognizeVariable(String name, FlowType type) {
        variables.put(name, new VariableInfo(availableCell, type));
    }

    public void declareVariable(String name, FlowType type) {
        recognizeVariable(name, type);
        storeVariable(name);
    }

    public void loadVariable(String name, FlowType expectedType) {
        if (!variables.containsKey(name)) {
            throw new IllegalArgumentException("Variable '" + name + "' does not exist");
        }
        final VariableInfo varInfo = variables.get(name);

        mv.visitVarInsn(
            getLoadOpCode(varInfo.type()),
            varInfo.index
        );

        if (expectedType != null) {
            if (BoxMapper.needBoxing(varInfo.type, expectedType)) {
                BoxMapper.box(varInfo.type, mv);
            } else if (BoxMapper.needUnboxing(varInfo.type, expectedType)) {
                BoxMapper.unbox(varInfo.type, mv);
            }
        }
    }

    public void storeVariable(String name) {
        VariableInfo varInfo = variables.get(name);
        if (varInfo == null) {
            throw new IllegalArgumentException("Variable '" + name + "' does not exist");
        }

        if (BoxMapper.needUnboxing(varInfo.type)) {
            BoxMapper.unbox(varInfo.type, mv);
        }

        int opcode = getStoreOpcode(varInfo.type);
        mv.visitVarInsn(opcode, varInfo.index);
        availableCell++;
    }

    private int getStoreOpcode(FlowType type) {
        if (type.isNullable) return Opcodes.ASTORE;

        return switch (type.name) {
            case "Int", "Bool", "Byte", "Short", "Char" -> Opcodes.ISTORE;
            case "Float" -> Opcodes.FSTORE;
            case "Double" -> Opcodes.DSTORE;
            case "Long" -> Opcodes.LSTORE;
            default -> Opcodes.ASTORE;
        };
    }

    private int getLoadOpCode(FlowType type) {
        if (type.isNullable) return Opcodes.ALOAD;

        return switch (type.name) {
            case "Int", "Bool", "Byte", "Short", "Char" -> Opcodes.ILOAD;
            case "Float" -> Opcodes.FLOAD;
            case "Double" -> Opcodes.DLOAD;
            case "Long" -> Opcodes.LLOAD;
            default -> Opcodes.ALOAD;
        };
    }

    private record VariableInfo(int index, FlowType type) {}
}
