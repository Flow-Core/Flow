package parser.nodes.generics;

import parser.nodes.FlowType;
import parser.nodes.classes.TypeDeclarationNode;

public class TypeParameterNode extends TypeDeclarationNode {
    public FlowType bound;

    public TypeParameterNode(String name, FlowType bound) {
        this.name = name;
        this.bound = bound;
    }

    public TypeParameterNode(String name) {
        this.name = name;
        bound = new FlowType("Thing", false, false);
    }

    public void updateBound(TypeDeclarationNode bound) {
        methods = bound.methods;
        typeParameters = bound.typeParameters;
    }

    @Override
    public String toString() {
        return bound == null ? name : name + ": " + bound;
    }
}