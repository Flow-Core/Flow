package parser.nodes;

import parser.nodes.components.ArgumentNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public class FunctionDeclarationNode implements ASTNode {
    private final String name;
    private final String returnType;
    private final List<ArgumentNode> args;
    private final BlockNode block;

    public FunctionDeclarationNode(String name, String returnType, List<ArgumentNode> args, BlockNode block) {
        this.name = name;
        this.returnType = returnType;
        this.args = args;
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<ArgumentNode> getArgs() {
        return args;
    }

    public BlockNode getBlock() {
        return block;
    }
}
