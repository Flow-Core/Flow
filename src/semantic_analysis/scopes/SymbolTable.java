package semantic_analysis.scopes;

import parser.nodes.ASTNode;
import parser.nodes.classes.*;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.visitors.ExpressionTraverse.TypeWrapper;

import java.util.*;

public record SymbolTable(
    List<InterfaceNode> interfaces,
    List<ClassDeclarationNode> classes,
    List<FunctionDeclarationNode> functions,
    List<FieldNode> fields,
    Map<ASTNode, String> bindingContext
) {
    public static SymbolTable getEmptySymbolTable() {
        return new SymbolTable(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new IdentityHashMap<>());
    }

    public boolean isSameType(TypeWrapper type, TypeWrapper superType) {
        if (!superType.isNullable() && type.isNullable())
            return false;

        if (Objects.equals(type.type(), superType.type())) {
            return true;
        }

        final ClassDeclarationNode classDeclarationNode = getClass(type.type());
        if (classDeclarationNode != null) {
            if (!classDeclarationNode.baseClasses.isEmpty() && classDeclarationNode.baseClasses.get(0).name.equals(superType.type())) {
                return true;
            }
            if (!classDeclarationNode.baseClasses.isEmpty() &&
                isSameType(
                    new TypeWrapper(
                        classDeclarationNode.baseClasses.get(0).name,
                        false,
                        type.isNullable()
                    ),
                    superType)
            ) {
                return true;
            }
        }

        final TypeDeclarationNode typeDeclarationNode = getTypeDeclaration(type.type());
        if (typeDeclarationNode != null) {
            final TypeDeclarationNode superTypeDeclaration = getTypeDeclaration(superType.type());

            if (typeDeclarationNode.equals(superTypeDeclaration)) {
                return true;
            }

            for (final BaseInterfaceNode baseInterfaceNode : getTypeDeclaration(type.type()).implementedInterfaces) {
                if (baseInterfaceNode.name.equals(superType.type()) ||
                    isSameType(
                        new TypeWrapper(
                            baseInterfaceNode.name,
                            false,
                            type.isNullable()
                        ),
                        superType
                    )
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    public void recognizeSymbolTable(SymbolTable other) {
        classes().addAll(other.classes());
        interfaces().addAll(other.interfaces());
        functions().addAll(other.functions());
        fields().addAll(other.fields());
        bindingContext.putAll(other.bindingContext);
    }

    public void addToBindingContext(SymbolTable other, String modulePath) {
        other.classes().forEach(classDeclarationNode -> bindingContext.put(classDeclarationNode, joinPath(modulePath, classDeclarationNode.name)));
        other.interfaces().forEach(interfaceNode -> bindingContext.put(interfaceNode, joinPath(modulePath, interfaceNode.name)));
        other.functions().forEach(functionDeclarationNode -> bindingContext.put(functionDeclarationNode, joinPath(modulePath,functionDeclarationNode.name)));
        other.fields().forEach(fieldNode -> bindingContext.put(fieldNode, joinPath(modulePath, fieldNode.initialization.declaration.name)));
    }

    public static String joinPath(String modulePath, String moduleName) {
        String path;
        if (modulePath.isEmpty()) {
            path = moduleName;
        } else {
            path = modulePath + "." + moduleName;
        }

        return path;
    }

    public boolean findSymbol(String symbol) {
        return findInterface(symbol) || findClass(symbol) || findFunction(symbol) || findField(symbol);
    }

    public ClassDeclarationNode getClass(String symbol) {
        if (symbol.contains(".")) {
            getTypeFromFQName(symbol);
        }

        return classes().stream().filter(
            classDeclarationNode -> classDeclarationNode.name.equals(symbol)
        ).findFirst().orElse(null);
    }

    public InterfaceNode getInterface(String symbol) {
        if (symbol.contains(".")) {
            getTypeFromFQName(symbol);
        }

        return interfaces().stream().filter(
            interfaceNode -> interfaceNode.name.equals(symbol)
        ).findFirst().orElse(null);
    }

    private TypeDeclarationNode getTypeFromFQName(String symbol) {
        System.out.println(bindingContext);
        System.out.println(symbol);
        var type = bindingContext.entrySet().stream().filter(
            entry -> entry.getValue().equals(symbol)
        ).findFirst().orElse(null);

        if (type == null) {
            return null;
        }

        return (TypeDeclarationNode) type.getKey();
    }

    private TypeDeclarationNode getTypeFromSimpleName(String symbol) {
        var type = bindingContext.keySet().stream().filter(
            key -> {
                if (key instanceof ClassDeclarationNode classDeclarationNode) {
                    return classDeclarationNode.name.equals(symbol);
                } else if (key instanceof InterfaceNode interfaceNode) {
                    return interfaceNode.name.equals(symbol);
                }

                return false;
            }
        ).findFirst().orElse(null);

        if (type == null) {
            return null;
        }

        return (TypeDeclarationNode) type;
    }

    public TypeDeclarationNode getTypeDeclaration(String symbol) {
        TypeDeclarationNode type = getClass(symbol);

        if (type == null) {
            type = getInterface(symbol);
        }

        if (type == null) {
            getTypeFromSimpleName(symbol);
        }

        return type;
    }

    public FunctionDeclarationNode getFunction(String symbol) {
        return functions().stream().filter(
            interfaceNode -> interfaceNode.name.equals(symbol)
        ).findFirst().orElse(null);
    }

    public FieldNode getField(String symbol) {
        return fields().stream().filter(
            interfaceNode -> interfaceNode.initialization.declaration.name.equals(symbol)
        ).findFirst().orElse(null);
    }

    public boolean findClass(String symbol) {
        return classes().stream().anyMatch(
            existingClass -> existingClass.name.equals(symbol)
        );
    }

    public boolean findInterface(String symbol) {
        return interfaces().stream().anyMatch(
            existingInterface -> existingInterface.name.equals(symbol)
        );
    }

    public boolean findTypeDeclaration(String symbol) {
        return findClass(symbol) || findInterface(symbol);
    }

    public boolean findFunction(String symbol) {
        return functions().stream().anyMatch(
            existingFunction -> existingFunction.name.equals(symbol)
        );
    }

    public boolean findField(String symbol) {
        return fields().stream().anyMatch(
            existingField -> existingField.initialization.declaration.name.equals(symbol)
        );
    }
}
