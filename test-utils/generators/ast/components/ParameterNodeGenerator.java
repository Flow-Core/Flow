package generators.ast.components;

import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionBaseNode;

public class ParameterNodeGenerator {
    private String type = "Int";
    private boolean isNullable = false;
    private String name = "param";
    private ExpressionBaseNode defaultValue = null;

    public static ParameterNodeGenerator builder() {
        return new ParameterNodeGenerator();
    }

    public ParameterNodeGenerator type(String type) {
        this.type = type;
        return this;
    }

    public ParameterNodeGenerator nullable(boolean nullable) {
        this.isNullable = nullable;
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
        return new ParameterNode(type, isNullable, name, defaultValue);
    }
}
