package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTNode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModifierLoader {
    public static void load(final ASTNode node, final List<String> modifiers, final ModifierType modifierType) {
        checkModifiers(node, modifiers, modifierType.restrictions);
    }

    public static void load(final List<String> modifiers, final ModifierType modifierType) {
        checkModifiers(null, modifiers, modifierType.restrictions);
    }

    private static void checkModifiers(
        ASTNode node,
        List<String> modifiers,
        Map<String, List<String>> allowedRules
    ) {
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();

        for (String mod : modifiers) {
            if (!seen.add(mod)) {
                duplicates.add(mod);
            }
        }

        if (!duplicates.isEmpty()) {
            LoggerFacade.error("Repeated modifiers: " + duplicates, node);
        }

        for (String modifier : modifiers) {
            if (!allowedRules.containsKey(modifier)) {
                LoggerFacade.error("Modifier '" + modifier + "' is not allowed here" + duplicates, node);
                return;
            }

            List<String> conflictingModifiers = allowedRules.get(modifier);
            for (String conflict : conflictingModifiers) {
                if (modifiers.contains(conflict)) {
                    LoggerFacade.error("Modifier '" + modifier + "' cannot be used with: " + duplicates, node);
                }
            }
        }
    }

    public static String getAccessModifier(List<String> modifiers) {
        return modifiers.stream()
            .filter(modifier -> modifier.equals("private") || modifier.equals("public") || modifier.equals("protected"))
            .findFirst().orElse("public");
    }

    public static boolean isPublic(List<String> modifiers) {
        return !modifiers.contains("private") && !modifiers.contains("protected");
    }

    public static boolean isDefaultPublic(List<String> modifiers) {
        return isPublic(modifiers) && !modifiers.contains("public");
    }

    public enum ModifierType {
        CLASS(
            Map.of(
                "private", List.of("public", "protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private"),
                "abstract", List.of("final", "open"),
                "final", List.of("abstract", "open"),
                "open", List.of("abstract", "final"),
                "data", List.of("abstract", "open", "final")
            )
        ),
        INTERFACE(
            Map.of(
                "private", List.of("public", "protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private")
            )
        ),
        FUNCTION(
            Map.of(
                "private", List.of("public", "protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private"),
                "static", List.of("abstract", "final", "open"),
                "abstract", List.of("static", "final", "open"),
                "final", List.of("static", "abstract", "open"),
                "open", List.of("static", "abstract", "final"),
                "override", List.of("abstract", "static", "final")
            )
        ),
        FUNCTION_INTERFACE(
            Map.of(
                "private", List.of("public, protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private")
            )
        ),
        CLASS_FIELD(
            Map.of(
                "private", List.of("public", "protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private"),
                "static", List.of("abstract", "final", "open"),
                "abstract", List.of("static", "final", "open"),
                "override", List.of("abstract", "static", "final")
            )
        ),
        TOP_LEVEL_FIELD(
            Map.of(
                "private", List.of("public", "protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private"),
                "static", List.of("abstract", "final", "open")
            )
        ),
        CONSTRUCTOR(
            Map.of(
                "private", List.of("public", "protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private")
            )
        );

        private final Map<String, List<String>> restrictions;

        ModifierType(Map<String, List<String>> restrictions) {
            this.restrictions = restrictions;
        }
    }
}

