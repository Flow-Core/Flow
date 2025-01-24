package semantic_analysis;

import parser.nodes.components.BlockNode;
import semantic_analysis.visitors.ClassLoaderVisitor;
import semantic_analysis.visitors.ClassSignatureVisitor;

import java.util.ArrayList;

public class SemanticAnalysis {
    final BlockNode root;
    final SymbolTable currSymbols = new SymbolTable(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    public SemanticAnalysis(final BlockNode root) {
        this.root = root;
    }

    public SymbolTable analyze() {
        root.accept(new ClassSignatureVisitor(), currSymbols);
        root.accept(new ClassLoaderVisitor(), currSymbols);

        return currSymbols;
    }
}
