package parser.nodes;

import java.util.List;

public class ClassDeclarationNode implements ASTNode {
    private final String name;
    private final String baseClass;
    private final List<ASTNode> fields;
    private final List<ASTNode> methods;

    public ClassDeclarationNode(
        final String name,
        final String baseClass,
        final List<ASTNode> fields,
        final List<ASTNode> methods
    ) {
        this.name = name;
        this.baseClass = baseClass;
        this.fields = fields;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public List<ASTNode> getFields() {
        return fields;
    }

    public List<ASTNode> getMethods() {
        return methods;
    }
}
