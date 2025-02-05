package generators.ast.classes;

import parser.nodes.classes.BaseInterfaceNode;

public class BaseInterfaceNodeGenerator {
    private String name = "BaseInterface";

    public static BaseInterfaceNodeGenerator builder() {
        return new BaseInterfaceNodeGenerator();
    }

    public BaseInterfaceNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public BaseInterfaceNode build() {
        return new BaseInterfaceNode(name);
    }
}
