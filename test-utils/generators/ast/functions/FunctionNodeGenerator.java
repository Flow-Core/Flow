package generators.ast.functions;

import parser.nodes.FlowType;
import parser.nodes.components.BlockNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class FunctionNodeGenerator {
    private String name = "testFunction";
    private FlowType returnType = new FlowType("Void", false, true);
    private List<String> modifiers = new ArrayList<>();
    private List<ParameterNode> parameters = new ArrayList<>();
    private BlockNode block = new BlockNode(new ArrayList<>());

    public static FunctionNodeGenerator builder() {
        return new FunctionNodeGenerator();
    }

    public FunctionNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public FunctionNodeGenerator returnType(FlowType returnType) {
        this.returnType = returnType;
        return this;
    }

    public FunctionNodeGenerator modifiers(List<String> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public FunctionNodeGenerator parameters(List<ParameterNode> parameters) {
        this.parameters = parameters;
        return this;
    }

    public FunctionNodeGenerator block(BlockNode block) {
        this.block = block;
        return this;
    }

    public FunctionDeclarationNode build() {
        return new FunctionDeclarationNode(name, returnType, modifiers, parameters, block);
    }
}
