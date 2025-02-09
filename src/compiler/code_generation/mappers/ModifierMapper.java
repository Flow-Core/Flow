package compiler.code_generation.mappers;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

public class ModifierMapper {
    private static final Map<String, Integer> MODIFIER_MAP = Map.of(
        "public", Opcodes.ACC_PUBLIC,
        "private", Opcodes.ACC_PRIVATE,
        "protected", Opcodes.ACC_PROTECTED,
        "static", Opcodes.ACC_STATIC,
        "final", Opcodes.ACC_FINAL,
        "abstract", Opcodes.ACC_ABSTRACT,
        "data", Opcodes.ACC_RECORD,
        "sealed", Opcodes.ACC_ABSTRACT | Opcodes.ACC_FINAL
    );

    public static int map(List<String> modifiers) {
        int flags = 0;
        for (String modifier : modifiers) {
            flags |= MODIFIER_MAP.getOrDefault(modifier, 0);
        }
        return flags;
    }
}
