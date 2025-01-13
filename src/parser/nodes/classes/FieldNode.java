package parser.nodes.classes;

import parser.nodes.ASTNode;
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
}
