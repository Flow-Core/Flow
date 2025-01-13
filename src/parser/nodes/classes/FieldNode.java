package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.variable.InitializedVariable;

import java.util.List;

public class FieldNode implements ASTNode {
    public List<String> modifiers;
    public InitializedVariable initialization;

    public FieldNode(List<String> modifiers, InitializedVariable initialization
    ) {
        this.modifiers = modifiers;
        this.initialization = initialization;
    }
}
