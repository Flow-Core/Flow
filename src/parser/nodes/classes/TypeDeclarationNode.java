package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.functions.FunctionDeclarationNode;

import java.util.List;

public abstract class TypeDeclarationNode implements ASTNode {
    public List<FunctionDeclarationNode> methods;
    public List<BaseInterfaceNode> implementedInterfaces;
}
