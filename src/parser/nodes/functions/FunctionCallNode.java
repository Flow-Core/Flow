package parser.nodes.functions;

import parser.nodes.ASTVisitor;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionNode;

import java.util.List;

public class FunctionCallNode implements ExpressionNode {
    public String name;
    public List<ArgumentNode> arguments;

    public FunctionCallNode(String name, List<ArgumentNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        ExpressionNode.super.accept(visitor);

        for (final ArgumentNode argument : arguments) {
            argument.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "FunctionCallNode{" +
            "name='" + name + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
