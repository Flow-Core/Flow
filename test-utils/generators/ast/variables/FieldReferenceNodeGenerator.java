package generators.ast.variables;

import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.FieldReferenceNode;

public class FieldReferenceNodeGenerator {
    private String holderType;
    private String name;
    private ExpressionNode holder;

    public static FieldReferenceNodeGenerator builder() {
        return new FieldReferenceNodeGenerator();
    }

    public FieldReferenceNodeGenerator holderType(String holderType) {
        this.holderType = holderType;
        return this;
    }

    public FieldReferenceNodeGenerator name(String name) {
        this.name = name;
        return this;
    }

    public FieldReferenceNodeGenerator holder(ExpressionNode holder) {
        this.holder = holder;
        return this;
    }

    public FieldReferenceNode build() {
        return new FieldReferenceNode(holderType, name, holder);
    }
}