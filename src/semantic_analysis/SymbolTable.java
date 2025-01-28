package semantic_analysis;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.functions.FunctionDeclarationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SymbolTable(
    List<InterfaceNode> interfaces,
    List<ClassDeclarationNode> classes,
    List<FunctionDeclarationNode> functions,
    List<FieldNode> fields,
    Map<ASTNode, String> bindingContext
) {
    public static SymbolTable getEmptySymbolTable() {
        return new SymbolTable(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());
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
        return modulePath + "." + moduleName;
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
