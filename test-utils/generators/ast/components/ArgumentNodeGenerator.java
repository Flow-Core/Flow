package generators.ast.components;

import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionBaseNode;

public class ArgumentNodeGenerator {
    private String name;
    private ExpressionBaseNode value;

    public static ArgumentNodeGenerator builder() {
        return new ArgumentNodeGenerator();
    }

    public ArgumentNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public ArgumentNodeGenerator value(ExpressionBaseNode value) {
        this.value = value;
        return this;
    }

    public ArgumentNode build() {
        return new ArgumentNode(name, value);
    }
}