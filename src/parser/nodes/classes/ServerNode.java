package parser.nodes.classes;

import parser.nodes.FlowType;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeParameterNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerNode extends ClassDeclarationNode {
    public FlowType protocol;

    public ServerNode(
        String name,
        FlowType protocol,
        List<FieldNode> fields,
        List<FunctionDeclarationNode> methods,
        List<TypeParameterNode> typeParameters
    ) {
        super(name, new ArrayList<>(), typeParameters, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), fields, methods, new ArrayList<>(), null, null);
        this.protocol = protocol;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ServerNode that = (ServerNode) o;
        return Objects.equals(protocol, that.protocol);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(protocol);
        return result;
    }
}
