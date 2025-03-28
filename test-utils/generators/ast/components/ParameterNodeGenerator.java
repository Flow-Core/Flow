package generators.ast.components;

import parser.nodes.FlowType;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionBaseNode;

public class ParameterNodeGenerator {
    private FlowType type = new FlowType("Int", false, true);
    private String name = "param";
    private ExpressionBaseNode defaultValue = null;

    public static ParameterNodeGenerator builder() {
        return new ParameterNodeGenerator();
    }

    public ParameterNodeGenerator type(FlowType type) {
        this.type = type;
        return this;
    }

    public ParameterNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public ParameterNodeGenerator defaultValue(ExpressionBaseNode defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ParameterNode build() {
        return new ParameterNode(type, name, defaultValue);
    }
}
