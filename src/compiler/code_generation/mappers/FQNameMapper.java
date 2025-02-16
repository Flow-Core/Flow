package compiler.code_generation.mappers;

import parser.nodes.ASTNode;
import semantic_analysis.scopes.Scope;

public class FQNameMapper {
    public static String getFQName(ASTNode node, Scope scope) {
        String fqName = scope.getFQName(node);
        if (fqName == null) {
            throw new IllegalArgumentException("Class should be loaded in the binding context");
        }

        return map(ExportTypeMapper.map(fqName));
    }

    public static String getFQName(String name, Scope scope) {
        if (name.contains(".")) {
            return map(name);
        }

        ASTNode node = scope.getTypeDeclaration(name);
        if (node == null) {
            throw new IllegalArgumentException("Class should be loaded in the current scope");
        }

        return getFQName(node, scope);
    }

    private static String map(String name) {
        return name.replace('.', '/');
    }
}
