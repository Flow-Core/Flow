package semantic_analysis.loaders;

import semantic_analysis.exceptions.SA_SemanticError;

import java.util.*;
import java.util.stream.Collectors;

public class ModifierLoader {
    public static void load(final List<String> modifiers, final ModifierType modifierType) {
        checkModifiers(modifiers, modifierType.restrictions);
    }

    private static void checkModifiers(
        List<String> modifiers,
        Map<String, List<String>> allowedRules
    ) {
        Set<String> duplicates = modifiers.stream()
            .filter(mod -> Collections.frequency(modifiers, mod) > 1)
            .collect(Collectors.toSet());

        if (!duplicates.isEmpty()) {
            throw new SA_SemanticError("Repeated modifiers: " + duplicates);
        }

        for (String modifier : modifiers) {
            if (!allowedRules.containsKey(modifier)) {
                throw new SA_SemanticError("Modifier '" + modifier + "' is not allowed here");
            }

            List<String> conflictingModifiers = allowedRules.get(modifier);
            List<String> conflicts = modifiers.stream()
                .filter(conflictingModifiers::contains)
                .toList();

            if (!conflicts.isEmpty()) {
                throw new SA_SemanticError("Modifier '" + modifier + "' cannot be used with: " + conflicts);
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
