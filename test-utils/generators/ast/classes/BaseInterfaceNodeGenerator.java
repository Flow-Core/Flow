package generators.ast.classes;

import parser.nodes.FlowType;
import parser.nodes.classes.BaseInterfaceNode;

public class BaseInterfaceNodeGenerator {
    private FlowType name = new FlowType("BaseInterface", false, false);

    public static BaseInterfaceNodeGenerator builder() {
        return new BaseInterfaceNodeGenerator();
    }

    public BaseInterfaceNodeGenerator name(FlowType name) {
        this.name = name;
        return this;
    }

    public BaseInterfaceNode build() {
        return new BaseInterfaceNode(name);
    }
}
