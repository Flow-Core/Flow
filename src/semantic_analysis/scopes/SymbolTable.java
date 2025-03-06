package semantic_analysis.scopes;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public record SymbolTable(
    List<InterfaceNode> interfaces,
    List<ClassDeclarationNode> classes,
    List<FunctionDeclarationNode> functions,
    List<FieldNode> fields,
    List<TypeParameterNode> typeParameters,
    Map<ASTNode, String> bindingContext
) {
    public static SymbolTable getEmptySymbolTable() {
        return new SymbolTable(
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new IdentityHashMap<>()
        );
    }

    public void recognizeSymbolTable(SymbolTable other) {
        classes().addAll(other.classes());
        interfaces().addAll(other.interfaces());
        functions().addAll(other.functions());
        fields().addAll(other.fields());
        typeParameters().addAll(other.typeParameters());

        bindingContext.putAll(other.bindingContext);
    }

    public void addToBindingContext(SymbolTable other) {
        other.classes().forEach(classDeclarationNode -> bindingContext.put(classDeclarationNode, other.bindingContext.get(classDeclarationNode)));
        other.interfaces().forEach(interfaceNode -> bindingContext.put(interfaceNode, other.bindingContext.get(interfaceNode)));
        other.functions().forEach(functionDeclarationNode -> bindingContext.put(functionDeclarationNode, other.bindingContext.get(functionDeclarationNode)));
    }

    public static String getFlowPathName(String path, String fileName) {
        return joinPath(path, fileName + "Fl");
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
        return classes().stream().filter(
            classDeclarationNode -> classDeclarationNode.name.equals(symbol)
        ).findFirst().orElse(null);
    }

    public InterfaceNode getInterface(String symbol) {
        return interfaces().stream().filter(
            interfaceNode -> interfaceNode.name.equals(symbol)
        ).findFirst().orElse(null);
    }

    public TypeParameterNode getTypeParameter(String symbol) {
        return typeParameters().stream().filter(
            typeParameter -> typeParameter.name.equals(symbol)
        ).findFirst().orElse(null);
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
            type = getTypeParameter(symbol);
        }

        if (type == null) {
            type = getTypeFromSimpleName(symbol);
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
            field -> field.initialization.declaration.name.equals(symbol)
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

    public boolean findParameterType(String symbol) {
        return typeParameters().stream().anyMatch(
            typeParameter -> typeParameter.name.equals(symbol)
        );
    }

    public boolean findTypeDeclaration(String symbol) {
        return findClass(symbol) || findInterface(symbol) || findParameterType(symbol);
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
