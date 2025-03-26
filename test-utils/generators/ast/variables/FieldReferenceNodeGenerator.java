package generators.ast.variables;

import parser.nodes.FlowType;
import parser.nodes.classes.FieldNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.variable.FieldReferenceNode;

public class FieldReferenceNodeGenerator {
    private FlowType holderType;
    private String name;
    private ExpressionNode holder;
    private FlowType type;
    private FieldNode declaration;
    private boolean isStatic;

    public static FieldReferenceNodeGenerator builder() {
        return new FieldReferenceNodeGenerator();
    }

    public FieldReferenceNodeGenerator holderType(FlowType holderType) {
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

    public FieldReferenceNodeGenerator type(FlowType type) {
        this.type = type;
        return this;
    }

    public FieldReferenceNodeGenerator declaration(FieldNode declaration) {
        this.declaration = declaration;
        return this;
    }

    public FieldReferenceNodeGenerator isStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public FieldReferenceNode build() {
        return new FieldReferenceNode(holderType, name, holder, type, declaration, isStatic);
    }
}