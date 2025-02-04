package semantic_analysis.loaders;

import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.scopes.Scope;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModifierLoader {
    // allowed modifiers, each modifier contains list of modifiers it cannot be with
    private static final Map<String, List<String>> CLASS_ALLOWED = Map.of(
        "private", List.of("public", "protected"),
        "public", List.of("private", "protected"),
        "protected", List.of("public", "private"),
        "static", List.of("abstract", "final", "open"),
        "abstract", List.of("static", "final", "open"),
        "final", List.of("static", "abstract", "open"),
        "open", List.of("static", "abstract", "final")
    );

    private static final Map<String, List<String>> FUNCTION_ALLOWED = Map.of(
        "private", List.of("public", "protected"),
        "public", List.of("private", "protected"),
        "protected", List.of("public", "private"),
        "static", List.of("abstract", "final", "open"),
        "abstract", List.of("static", "final", "open"),
        "final", List.of("static", "abstract", "open"),
        "open", List.of("static", "abstract", "final"),
        "override", List.of("abstract", "static", "final")
    );

    public static void load(final List<String> modifiers, final Scope.Type type) {
        if (type == Scope.Type.TOP) {
            throw new IllegalArgumentException("No top level modifiers exist");
        }

        if (type == Scope.Type.CLASS) {
            checkModifiers(modifiers, CLASS_ALLOWED);
        } else if (type == Scope.Type.FUNCTION) {
            checkModifiers(modifiers, FUNCTION_ALLOWED);
        }
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
}
