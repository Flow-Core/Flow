package generators.scopes;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.scopes.SymbolTable;

import java.util.*;

public class SymbolTableGenerator {
    private List<InterfaceNode> interfaces = new ArrayList<>();
    private List<ClassDeclarationNode> classes = new ArrayList<>();
    private List<FunctionDeclarationNode> functions = new ArrayList<>();
    private List<FieldNode> fields = new ArrayList<>();
    private List<TypeParameterNode> typeParameters = new ArrayList<>();
    private Map<ASTNode, String> bindingContext = new HashMap<>();

    public static SymbolTableGenerator builder() {
        return new SymbolTableGenerator();
    }

    public SymbolTableGenerator interfaces(List<InterfaceNode> interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    public SymbolTableGenerator classes(List<ClassDeclarationNode> classes) {
        this.classes = classes;
        return this;
    }

    public SymbolTableGenerator functions(List<FunctionDeclarationNode> functions) {
        this.functions = functions;
        return this;
    }

    public SymbolTableGenerator fields(List<FieldNode> fields) {
        this.fields = fields;
        return this;
    }

    public SymbolTableGenerator typeParameters(List<TypeParameterNode> typeParameters) {
        this.typeParameters = typeParameters;
        return this;
    }

    public SymbolTableGenerator bindingContext(Map<ASTNode, String> bindingContext) {
        this.bindingContext = bindingContext;
        return this;
    }

    public SymbolTable build() {
        return new SymbolTable(interfaces, classes, functions, fields, typeParameters, bindingContext);
    }
}
