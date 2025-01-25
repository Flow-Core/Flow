package semantic_analysis;

import semantic_analysis.visitors.ClassSignatureVisitor;

import java.util.List;

public class SemanticAnalysis {
    final List<FileWrapper> files;

    public SemanticAnalysis(final List<FileWrapper> files) {
        this.files = files;
    }

    public void analyze() {
        for (final FileWrapper file : files) {
            file.root().accept(new ClassSignatureVisitor(), file.symbolTable());
        }
    }
}
