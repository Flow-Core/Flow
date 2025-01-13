package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;

import java.util.List;

public class ClassDeclarationNode implements ASTNode {
    public String name;
    public List<ParameterNode> primaryConstructor;
    public String baseClass;
    public List<ArgumentNode> baseClassArguments;
    public List<String> interfaces;
    public List<FieldNode> fields;
    public List<FunctionDeclarationNode> methods;
    public List<ConstructorNode> constructors;
    public BlockNode initBlock;

    public ClassDeclarationNode(
        final String name,
        final List<ParameterNode> primaryConstructor,
        final String baseClass,
        final List<ArgumentNode> baseClassArguments,
        final List<String> interfaces,
        final List<FieldNode> fields,
        final List<FunctionDeclarationNode> methods,
        final List<ConstructorNode> constructors,
        final BlockNode initBlock
    ) {
        this.name = name;
        this.primaryConstructor = primaryConstructor;
        this.baseClass = baseClass;
        this.baseClassArguments = baseClassArguments;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.constructors = constructors;
        this.initBlock = initBlock;
    }
}