package compiler.code_generation.manager;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class VariableManager {
    private final Map<String, Integer> variables = new HashMap<>();
    private final MethodVisitor mv;
    private int availableCell = 0;

    public VariableManager(MethodVisitor mv) {
        this.mv = mv;
    }

    public void declareVariable(String name) {
        variables.put(name, availableCell);
        mv.visitVarInsn(Opcodes.ASTORE, availableCell++);
    }

    public int loadVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new IllegalArgumentException("Variable '" + name + "' does not exist");
        }

        return variables.get(name);
    }
}
