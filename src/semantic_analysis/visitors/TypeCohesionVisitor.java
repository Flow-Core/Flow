package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_UnresolvedSymbolException;

public class TypeCohesionVisitor implements ASTVisitor<SymbolTable> {
    private final SymbolTable packageLevel;

    public TypeCohesionVisitor(SymbolTable packageLevel) {
        this.packageLevel = packageLevel;
    }

    @Override
    public void visit(final ASTNode node, final SymbolTable data) {
        if (node instanceof FunctionDeclarationNode functionDeclarationNode) {
            functionHandler(functionDeclarationNode, data);
        } else if (node instanceof FieldNode fieldNode) {
            fieldHandler(fieldNode, data);
        } else if (node instanceof ParameterNode parameterNode) {
            parameterHandler(parameterNode, data);
        }
    }

    private void functionHandler(final FunctionDeclarationNode functionDeclarationNode, final SymbolTable data) {
        validateType(functionDeclarationNode.returnType, data);
    }

    private void fieldHandler(final FieldNode fieldNode, final SymbolTable data) {
        validateType(fieldNode.initialization.declaration.type, data);
    }

    private void parameterHandler(final ParameterNode parameterNode, final SymbolTable data) {
        validateType(parameterNode.type, data);
    }

    private void validateType(final String type, final SymbolTable data) {
        if (
            !data.findClass(type)
                && !data.findInterface(type)
                && !packageLevel.findClass(type)
                && !packageLevel.findInterface(type)
        ) {
            throw new SA_UnresolvedSymbolException(type);
        }
    }
}
