package parser.nodes.functions;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.components.ParameterNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public class FunctionDeclarationNode implements ASTNode {
    public String name;
    public String returnType;
    public boolean isReturnTypeNullable;
    public List<String> modifiers;
    public List<ParameterNode> parameters;
    public BlockNode block;

    public FunctionDeclarationNode(String name, String returnType, boolean isReturnTypeNullable, List<String> modifiers, List<ParameterNode> parameters, BlockNode block) {
        this.name = name;
        this.returnType = returnType;
        this.isReturnTypeNullable = isReturnTypeNullable;
        this.modifiers = modifiers;
        this.parameters = parameters;
        this.block = block;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        ASTNode.super.accept(visitor, data);

        if (block != null) {
            block.accept(visitor, data);
        }
    }

    @Override
    public String toString() {
        return "FunctionDeclarationNode{" +
            "name='" + name + '\'' +
            ", returnType='" + returnType + '\'' +
            ", isReturnTypeNullable=" + isReturnTypeNullable +
            ", modifiers=" + modifiers +
            ", parameters=" + parameters +
            ", block=" + block +
            '}';
    }
}
