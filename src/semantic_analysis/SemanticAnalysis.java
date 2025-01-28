package semantic_analysis;

import semantic_analysis.loaders.ClassLoader;
import semantic_analysis.loaders.SignatureLoader;
import semantic_analysis.loaders.ImportLoader;

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
                new ImportLoader().load(file.root(), file.symbolTable(), packages);
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