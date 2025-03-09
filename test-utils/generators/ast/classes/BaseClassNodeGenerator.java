package generators.ast.classes;

import parser.nodes.FlowType;
import parser.nodes.classes.BaseClassNode;
import parser.nodes.components.ArgumentNode;

import java.util.ArrayList;
import java.util.List;

public class BaseClassNodeGenerator {
    private FlowType name = new FlowType("BaseClass", false, false);
    private List<ArgumentNode> arguments = new ArrayList<>();

    public static BaseClassNodeGenerator builder() {
        return new BaseClassNodeGenerator();
    }

    public BaseClassNodeGenerator name(FlowType name) {
        this.name = name;
        return this;
    }

    public BaseClassNodeGenerator arguments(List<ArgumentNode> arguments) {
        this.arguments = arguments;
        return this;
    }

    public BaseClassNode build() {
        return new BaseClassNode(name, arguments);
    }
}
