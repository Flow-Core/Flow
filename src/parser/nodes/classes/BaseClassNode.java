package parser.nodes.classes;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.components.ArgumentNode;

import java.util.List;

public class BaseClassNode implements ASTNode {
    public String name;
    public List<ArgumentNode> arguments;

    public BaseClassNode(String name, List<ArgumentNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void accept(final ASTVisitor visitor) {
        ASTNode.super.accept(visitor);

        for (final ArgumentNode argument : arguments) {
            argument.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "BaseClassNode{" +
            "name='" + name + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
