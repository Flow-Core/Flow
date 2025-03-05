package compiler.code_generation.mappers;

import compiler.code_generation.constants.CodeGenerationConstant;
import parser.nodes.ASTNode;
import parser.nodes.FlowType;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.generics.TypeArgument;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;

import java.util.List;

public class FQNameMapper {
    public static String getJVMName(FlowType type, Scope scope) {
        if (!type.isPrimitive && !type.shouldBePrimitive() && !type.name.equals("Void")) {
            return parseTypeArgumentSignature(type, scope);
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

    private static String parseTypeArgumentSignature(FlowType type, Scope scope) {
        StringBuilder jvmName = new StringBuilder();
        jvmName.append("L").append(FQNameMapper.getFQName(type.name, scope));

        if (!type.typeArguments.isEmpty()) {
            jvmName.append("<");
            for (final TypeArgument arg : type.typeArguments) {
                jvmName.append(parseTypeArgumentSignature(arg.type, scope));
            }
            jvmName.append(">");
        }

        jvmName.append(";");
        return jvmName.toString();
    }

    public static String parseTypeParameterSignature(List<TypeParameterNode> typeParameters, Scope scope) {
        if (typeParameters.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder("<");
        for (TypeParameterNode parameter : typeParameters) {
            sb.append(parameter.name);
            sb.append(":");
            if (parameter.bound != null) {
                sb.append(getJVMName(parameter.bound, scope));
            } else {
                sb.append("L").append(CodeGenerationConstant.baseObjectFQName).append(";");
            }
        }
        sb.append(">");
        return sb.toString();
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

        TypeDeclarationNode node = TypeRecognize.getTypeDeclaration(name, scope);
        if (node == null) {
            throw new IllegalArgumentException("Class should be loaded in the current scope");
        }

        if (node instanceof TypeParameterNode typeParameterNode) {
            return "T" + typeParameterNode.name;
        }

        return getFQName(node, scope);
    }

    private static String map(String name) {
        return name.replace('.', '/');
    }
}
