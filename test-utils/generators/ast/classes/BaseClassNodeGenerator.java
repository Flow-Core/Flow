package generators.ast.classes;

import parser.nodes.classes.BaseClassNode;
import parser.nodes.components.ArgumentNode;

import java.util.ArrayList;
import java.util.List;

public class BaseClassNodeGenerator {
    private String name = "BaseClass";
    private List<ArgumentNode> arguments = new ArrayList<>();

    public static BaseClassNodeGenerator builder() {
        return new BaseClassNodeGenerator();
    }

    public BaseClassNodeGenerator name(String name) {
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
