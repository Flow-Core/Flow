package semantic_analysis.loaders;

import semantic_analysis.exceptions.SA_SemanticError;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModifierLoader {
    public static void load(final List<String> modifiers, final ModifierType modifierType) {
        checkModifiers(modifiers, modifierType.restrictions);
    }

    private static void checkModifiers(
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
            throw new SA_SemanticError("Repeated modifiers: " + duplicates);
        }

        for (String modifier : modifiers) {
            if (!allowedRules.containsKey(modifier)) {
                throw new SA_SemanticError("Modifier '" + modifier + "' is not allowed here");
            }

            List<String> conflictingModifiers = allowedRules.get(modifier);
            for (String conflict : conflictingModifiers) {
                if (modifiers.contains(conflict)) {
                    throw new SA_SemanticError("Modifier '" + modifier + "' cannot be used with: " + conflict);
                }
            }
        }
    }

    public enum ModifierType {
        CLASS(
            Map.of(
                "private", List.of("public", "protected"),
                "public", List.of("private", "protected"),
                "protected", List.of("public", "private"),
                "static", List.of("abstract", "final", "open"),
                "abstract", List.of("static", "final", "open"),
                "final", List.of("static", "abstract", "open"),
                "open", List.of("static", "abstract", "final")
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

