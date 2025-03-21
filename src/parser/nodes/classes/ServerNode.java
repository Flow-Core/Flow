package parser.nodes.classes;

import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.functions.FunctionDeclarationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerNode extends ClassDeclarationNode {
    public FlowType protocol;

    public ServerNode(
        String name,
        FlowType protocol,
        List<FieldNode> fields,
        List<FunctionDeclarationNode> methods
    ) {
        super(name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), fields, methods, new ArrayList<>(), null, null);
        this.protocol = protocol;
    }

    @Override
    public <D> void accept(ASTVisitor<D> visitor, D data) {
        super.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServerNode that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(protocol, that.protocol) && Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), protocol, fields);
    }
}
