package parser.nodes.classes;

public class BaseInterfaceNode {
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
