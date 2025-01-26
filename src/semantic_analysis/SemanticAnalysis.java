package semantic_analysis;

import parser.nodes.components.BlockNode;
import semantic_analysis.visitors.ClassSignatureVisitor;

import java.util.Map;

public class SemanticAnalysis {
    final Map<String, Package> packages;

    public SemanticAnalysis(final Map<String, Package> packages) {
        this.packages = packages;
    }

    public void analyze() {
        for (final Package currentPackage : packages.values()) {
            for (final BlockNode file : currentPackage.files()) {
                file.accept(new ClassSignatureVisitor(), currentPackage.symbolTable());
            }
        }
    }
}
