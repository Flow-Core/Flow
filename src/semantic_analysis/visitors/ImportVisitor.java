package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.ASTVisitor;
import parser.nodes.packages.ImportNode;
import semantic_analysis.PackageWrapper;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedPackageException;

import java.util.Map;

public class ImportVisitor implements ASTVisitor<SymbolTable> {
    private final Map<String, PackageWrapper> globalPackages;

    public ImportVisitor(Map<String, PackageWrapper> globalPackages) {
        this.globalPackages = globalPackages;
    }

    @Override
    public void visit(final ASTNode node, final SymbolTable data) {
        if (node instanceof ImportNode importNode) {
            validateImport(importNode, data);
        }
    }

    private void validateImport(final ImportNode importNode, final SymbolTable data) {
        final String modulePath = importNode.module;

        final int lastDotIndex = modulePath.lastIndexOf(".");
        final String packagePath = modulePath.substring(0, lastDotIndex);
        final String module = modulePath.substring(lastDotIndex + 1);

        if (!globalPackages.containsKey(packagePath)) {
            throw new SA_UnresolvedPackageException(packagePath);
        }

        final SymbolTable importedSymbols = globalPackages.get(packagePath).symbolTable();
        if (importNode.isWildcard) {
            // Add all classes and interfaces from the imported package
            data.classes().addAll(importedSymbols.classes());
            data.interfaces().addAll(importedSymbols.interfaces());
        } else {
            // Add only the needed module
            var optionalClass = importedSymbols.classes().stream()
                .filter(currentClass -> currentClass.name.equals(module))
                .findFirst();

            if (optionalClass.isPresent()) {
                data.classes().add(optionalClass.get());
                return;
            }

            var optionalInterface = importedSymbols.interfaces().stream()
                .filter(currentInterface -> currentInterface.name.equals(module))
                .findFirst();

            if (optionalInterface.isPresent()) {
                data.interfaces().add(optionalInterface.get());
                return;
            }

            throw new SA_SemanticError("Symbol not found in package: " + module);
        }
    }
}
