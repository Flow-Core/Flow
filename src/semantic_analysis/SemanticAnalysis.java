package semantic_analysis;

import parser.nodes.FunctionDeclarationNode;
import parser.nodes.components.BlockNode;

import java.util.List;

public class SemanticAnalysis {
    public SemanticAnalysis(BlockNode treeRoot) {
        List<FunctionDeclarationNode> functions = SymbolCohesionCheck.performCheck(treeRoot);

    }
}
