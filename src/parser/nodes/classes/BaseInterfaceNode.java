package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.FlowType;

import java.util.Objects;

public class BaseInterfaceNode implements ASTNode {
    public FlowType type;

    public BaseInterfaceNode(FlowType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BaseInterfaceNode that = (BaseInterfaceNode) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }
}
