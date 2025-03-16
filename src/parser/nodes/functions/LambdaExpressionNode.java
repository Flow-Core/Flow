package parser.nodes.functions;

import parser.nodes.FlowType;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

public class LambdaExpressionNode extends FunctionDeclarationNode implements ExpressionNode {
    static int lambdaCounter = 0;

    public LambdaExpressionNode(FlowType returnType, List<String> modifiers, List<ParameterNode> parameters, BodyNode body) {
        super("Lambda$" + lambdaCounter, returnType, modifiers, parameters, new ArrayList<>(), body);
    }

    @Override
    public String toString() {
        return "LambdaExpressionNode{" +
            "name='" + name + '\'' +
            ", returnType=" + returnType +
            ", modifiers=" + modifiers +
            ", parameters=" + parameters +
            ", typeParameters=" + typeParameters +
            ", body=" + body +
            '}';
    }
}
