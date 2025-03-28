package generators.ast.variables;

import parser.nodes.variable.InitializedVariableNode;
import parser.nodes.variable.VariableAssignmentNode;
import parser.nodes.variable.VariableDeclarationNode;

public class InitializedVariableNodeGenerator {
    private VariableDeclarationNode declaration;
    private VariableAssignmentNode assignment;

    public static InitializedVariableNodeGenerator builder() {
        return new InitializedVariableNodeGenerator();
    }

    public InitializedVariableNodeGenerator declaration(VariableDeclarationNode declaration) {
        this.declaration = declaration;
        return this;
    }

    public InitializedVariableNodeGenerator assignment(VariableAssignmentNode assignment) {
        this.assignment = assignment;
        return this;
    }

    public InitializedVariableNode build() {
        return new InitializedVariableNode(declaration, assignment);
    }
}
