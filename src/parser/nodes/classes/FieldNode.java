package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.variable.InitializedVariableNode;

import java.util.List;

public class FieldNode implements ASTNode {
    public List<String> modifiers;
    public InitializedVariableNode initialization;

    public FieldNode(List<String> modifiers, InitializedVariableNode initialization
    ) {
        this.modifiers = modifiers;
        this.initialization = initialization;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        initialization.accept(visitor);
    }

    @Override
    public String toString() {
        return "FieldNode{" +
            "modifiers=" + modifiers +
            ", initialization=" + initialization +
            '}';
    }
}
