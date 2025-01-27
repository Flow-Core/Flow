package semantic_analysis;

import semantic_analysis.visitors.ClassLoader;
import semantic_analysis.visitors.SignatureLoader;
import semantic_analysis.visitors.ImportVisitor;

import java.util.Map;

public class SemanticAnalysis {
    final Map<String, PackageWrapper> packages;

    public SemanticAnalysis(final Map<String, PackageWrapper> packages) {
        this.packages = packages;
    }

    public void analyze() {
        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                SignatureLoader.load(file.root().children, file.symbolTable(), currentPackageWrapper);
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                file.root().accept(new ImportVisitor(packages), file.symbolTable());
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                file.root().accept(new ClassLoader(currentPackageWrapper.symbolTable()), file.symbolTable());
            }
        }

        // Type cohesion -- should be the last check
        // doesn't work on tests - standard library doesn't exist
        /*
        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                file.root().accept(new TypeCohesionVisitor(currentPackageWrapper.symbolTable()), file.symbolTable());
            }
        }
        */
    }
}