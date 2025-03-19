package parser.nodes.functions;

import parser.nodes.ASTVisitor;
import parser.nodes.FlowType;
import parser.nodes.expressions.ExpressionNode;

import java.util.Objects;

public class MethodReferenceNode implements ExpressionNode {
    public FlowType holderType;
    public FunctionDeclarationNode method;

    public MethodReferenceNode(FlowType holderType, FunctionDeclarationNode method) {
        this.method = method;
        this.holderType = holderType;
    }

    @Override
    public <D> void accept(ASTVisitor<D> visitor, D data) {
        ExpressionNode.super.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MethodReferenceNode that)) return false;
        return Objects.equals(holderType, that.holderType) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(holderType, method);
    }

    @Override
    public String toString() {
        return holderType + "::" + method.name;
    }
}
