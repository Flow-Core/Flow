package generators.ast.variables;

import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.FieldReferenceNode;
import semantic_analysis.visitors.ExpressionTraverse;

public class FieldReferenceNodeGenerator {
    private String holderType;
    private String name;
    private ExpressionNode holder;
    private ExpressionTraverse.TypeWrapper type;
    private boolean isStatic;

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

    public FieldReferenceNodeGenerator type(ExpressionTraverse.TypeWrapper type) {
        this.type = type;
        return this;
    }

    public FieldReferenceNodeGenerator isStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public FieldReferenceNode build() {
        return new FieldReferenceNode(holderType, name, holder, type, isStatic);
    }
}