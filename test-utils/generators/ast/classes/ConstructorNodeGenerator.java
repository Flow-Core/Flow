package generators.ast.classes;

import parser.nodes.classes.ConstructorNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;

import java.util.ArrayList;
import java.util.List;

public class ConstructorNodeGenerator {
    private String accessModifier = "public";
    private List<ParameterNode> parameters = new ArrayList<>();
    private BlockNode body = new BlockNode(new ArrayList<>());

    public static ConstructorNodeGenerator builder() {
        return new ConstructorNodeGenerator();
    }

    public ConstructorNodeGenerator accessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
        return this;
    }

    public ConstructorNodeGenerator parameters(List<ParameterNode> parameters) {
        this.parameters = parameters;
        return this;
    }

    public ConstructorNodeGenerator body(BlockNode body) {
        this.body = body;
        return this;
    }

    public ConstructorNode build() {
        return new ConstructorNode(accessModifier, parameters, new BodyNode(body));
    }
}
