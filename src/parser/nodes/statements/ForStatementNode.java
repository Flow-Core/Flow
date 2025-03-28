package parser.nodes.statements;

import parser.nodes.ASTVisitor;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BodyNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.variable.VariableAssignmentNode;

import java.util.Objects;

public class ForStatementNode implements StatementNode {
    public VariableAssignmentNode initialization;
    public FieldNode populatedInitialization;
    public ExpressionBaseNode condition;
    public BodyNode action;
    public BodyNode loopBlock;

    public ForStatementNode(VariableAssignmentNode initialization, ExpressionBaseNode condition, BodyNode action, BodyNode loopBlock) {
        this.initialization = initialization;
        this.condition = condition;
        this.action = action;
        this.loopBlock = loopBlock;
    }

    @Override
    public <D> void accept(final ASTVisitor<D> visitor, final D data) {
        StatementNode.super.accept(visitor, data);

        condition.accept(visitor, data);

        action.accept(visitor, data);

        loopBlock.accept(visitor, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForStatementNode that = (ForStatementNode) o;

        if (!Objects.equals(initialization, that.initialization))
            return false;
        if (!Objects.equals(condition, that.condition)) return false;
        if (!Objects.equals(action, that.action)) return false;
        return Objects.equals(loopBlock, that.loopBlock);
    }

    @Override
    public int hashCode() {
        int result = initialization != null ? initialization.hashCode() : 0;
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (loopBlock != null ? loopBlock.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ForStatementNode{" +
            "initialization=" + initialization +
            ", condition=" + condition +
            ", action=" + action +
            ", loopBlock=" + loopBlock +
            '}';
    }
}
