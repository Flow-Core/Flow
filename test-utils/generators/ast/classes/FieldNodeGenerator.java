package generators.ast.classes;

import parser.nodes.classes.FieldNode;
import parser.nodes.variable.InitializedVariableNode;

import java.util.ArrayList;
import java.util.List;

public class FieldNodeGenerator {
    private List<String> modifiers = new ArrayList<>();
    private InitializedVariableNode initialization;

    public static FieldNodeGenerator builder() {
        return new FieldNodeGenerator();
    }

    public FieldNodeGenerator modifiers(List<String> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public FieldNodeGenerator initialization(InitializedVariableNode initialization) {
        this.initialization = initialization;
        return this;
    }

    public FieldNode build() {
        return new FieldNode(modifiers, initialization);
    }
}
