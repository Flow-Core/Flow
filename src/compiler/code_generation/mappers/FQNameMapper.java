package compiler.code_generation.mappers;

import compiler.code_generation.constants.CodeGenerationConstant;
import parser.nodes.ASTNode;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.generics.TypeArgument;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;

import java.util.HashSet;
import java.util.List;

public class FQNameMapper {
    public static String getJVMName(FlowType type, Scope scope, List<TypeParameterNode> typeParameters) {
        return getJVMName(type, scope, typeParameters, false);
    }

    public static String getJVMName(FlowType type, Scope scope, List<TypeParameterNode> typeParameters, boolean forSignature) {
        final TypeParameterNode typeParameterNode = getTypeParameter(type.name, typeParameters);
        if (typeParameterNode != null) {
            if (forSignature) {
                return "T" + typeParameterNode.name + ";";
            } else {
                FlowType effectiveBound = TypeRecognize.getEffectiveType(typeParameterNode.bound, scope, new HashSet<>());
                return getJVMName(effectiveBound, scope, typeParameters, false);
            }
        }

        if (!type.isPrimitive && !type.shouldBePrimitive && !type.name.equals("Void")) {
            return parseTypeArgumentSignature(type, scope, typeParameters, forSignature);
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
            default -> "L" + getFQName(type.name, scope) + ";";
        };
    }

    private static String parseTypeArgumentSignature(FlowType type, Scope scope, List<TypeParameterNode> typeParameters, boolean forSignature) {
        StringBuilder jvmName = new StringBuilder();
        jvmName.append("L").append(getFQName(type.name, scope));

        if (!type.typeArguments.isEmpty() && forSignature) {
            jvmName.append("<");
            for (TypeArgument arg : type.typeArguments) {
                jvmName.append(getJVMName(arg.type, scope, typeParameters, true));
            }
            jvmName.append(">");
        }

        jvmName.append(";");
        return jvmName.toString();
    }

    public static String buildClassSignature(
        TypeDeclarationNode typeDeclarationNode,
        Scope scope
    ) {
        StringBuilder sb = new StringBuilder();

        if (!typeDeclarationNode.typeParameters.isEmpty()) {
            sb.append("<");
            for (TypeParameterNode param : typeDeclarationNode.typeParameters) {
                sb.append(param.name).append(":");
                if (param.bound != null) {
                    sb.append(getJVMName(param.bound, scope, typeDeclarationNode.typeParameters, true));
                } else {
                    sb.append("L").append(CodeGenerationConstant.baseObjectFQName).append(";");
                }
            }
            sb.append(">");
        }

        if (typeDeclarationNode instanceof ClassDeclarationNode classDeclarationNode && !classDeclarationNode.baseClasses.isEmpty()) {
            BaseClassNode baseClassNode = classDeclarationNode.baseClasses.get(0);

            sb.append("L").append(baseClassNode.type.name);
            if (!baseClassNode.type.typeArguments.isEmpty()) {
                sb.append("<");

                ClassDeclarationNode baseClassDeclaration = TypeRecognize.getClass(baseClassNode.type.name, scope);
                if (baseClassDeclaration == null) {
                    throw new RuntimeException("Class should be loaded in the current scope");
                }

                for (TypeArgument arg : baseClassNode.type.typeArguments) {
                    sb.append(getJVMName(arg.type, scope, baseClassDeclaration.typeParameters, true));
                }
                sb.append(">");
            }
            sb.append(";");
        }

        for (BaseInterfaceNode baseInterfaceNode : typeDeclarationNode.implementedInterfaces) {
            sb.append("L").append(baseInterfaceNode.type.name);
            if (!baseInterfaceNode.type.typeArguments.isEmpty()) {
                sb.append("<");

                InterfaceNode baseInterfaceDeclaration = TypeRecognize.getInterface(baseInterfaceNode.type.name, scope);
                if (baseInterfaceDeclaration == null) {
                    throw new RuntimeException("Interface should be loaded in the current scope");
                }

                for (TypeArgument arg : baseInterfaceNode.type.typeArguments) {
                    sb.append(getJVMName(arg.type, scope, baseInterfaceDeclaration.typeParameters, true));
                }
                sb.append(">");
            }
            sb.append(";");
        }

        return sb.toString();
    }

    public static String parseTypeParameterSignature(List<TypeParameterNode> typeParameters, Scope scope) {
        if (typeParameters.isEmpty()) return null;

        StringBuilder sb = new StringBuilder("<");
        for (TypeParameterNode parameter : typeParameters) {
            sb.append(parameter.name).append(":");
            if (parameter.bound != null) {
                sb.append(getJVMName(parameter.bound, scope, typeParameters, true));
            } else {
                sb.append("L").append(CodeGenerationConstant.baseObjectFQName).append(";");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    public static String getFQName(ASTNode node, Scope scope) {
        if (node instanceof TypeParameterNode typeParameterNode) {
            return map(TypeRecognize.getEffectiveType(typeParameterNode.bound, scope, new HashSet<>()).name);
        }

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

        return getFQName(node, scope);
    }

    private static String map(String name) {
        return name.replace('.', '/');
    }

    private static TypeParameterNode getTypeParameter(String name, List<TypeParameterNode> typeParameters) {
        return typeParameters.stream().filter(param -> param.name.equals(name)).findFirst().orElse(null);
    }
}
