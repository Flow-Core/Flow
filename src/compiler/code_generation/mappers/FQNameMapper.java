package compiler.code_generation.mappers;

import parser.nodes.ASTNode;
import parser.nodes.FlowType;
import semantic_analysis.scopes.Scope;

public class FQNameMapper {
    public static String getJVMName(FlowType type, Scope scope) {
        if (!type.isPrimitive && !type.shouldBePrimitive() && !type.name.equals("Void")) {
            return "L" + FQNameMapper.getFQName(type.name, scope) + ";";
        }

        return switch (type.name) {
            case "Void" -> "V";
            case "Int" -> "I";
            case "Bool" -> "Z";
            case "Float" -> "F";
            case "Double" -> "D";
            case "Long" -> "J";
            case "Byte" -> "B";
            case "Char" -> "C";
            case "Short" -> "S";
            default -> "L" + FQNameMapper.getFQName(type.name, scope) + ";";
        };
    }

    public static String getFQName(ASTNode node, Scope scope) {
        String fqName = scope.getFQName(node);
        if (fqName == null) {
            throw new IllegalArgumentException("Class should be loaded in the binding context");
        }

        return map(fqName);
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
