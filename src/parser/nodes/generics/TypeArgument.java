package parser.nodes.generics;

import parser.nodes.FlowType;

public class TypeArgument {
    public FlowType type;

    public TypeArgument(FlowType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "<" + type.toString() + ">";
    }
}
