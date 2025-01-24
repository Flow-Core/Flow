package parser.nodes.classes;

import parser.nodes.ASTNode;

public class BaseInterfaceNode implements ASTNode {
    public String name;

    public BaseInterfaceNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BaseInterfaceNode{" +
            "name='" + name + '\'' +
            '}';
    }
}
