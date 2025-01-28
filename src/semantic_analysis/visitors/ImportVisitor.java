package semantic_analysis.visitors;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;
import parser.nodes.packages.ImportNode;
import semantic_analysis.PackageWrapper;
import semantic_analysis.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedPackageException;

import java.util.Map;

public class ImportVisitor {

    public void visit(final BlockNode root, final SymbolTable data, final Map<String, PackageWrapper> globalPackages) {
        for (final ASTNode node : root.children) {
            if (node instanceof ImportNode importNode) {
                validateImport(importNode, data, globalPackages);
            }
        }
    }

    private void validateImport(final ImportNode importNode, final SymbolTable data, final Map<String, PackageWrapper> globalPackages) {
        final String modulePath = importNode.module;

        final int lastDotIndex = modulePath.lastIndexOf(".");
        final String packagePath = modulePath.substring(0, lastDotIndex);
        final String module = modulePath.substring(lastDotIndex + 1);

        if (!globalPackages.containsKey(packagePath)) {
            throw new SA_UnresolvedPackageException(packagePath);
        }

        final SymbolTable importedSymbols = globalPackages.get(packagePath).symbolTable();
        if (importNode.isWildcard) {
            if (!importNode.alias.equals("*")) {
                throw new SA_SemanticError("Cannot rename all imported items to one identifier");
            }

            data.recognizeSymbolTable(importedSymbols);
            data.addToBindingContext(importedSymbols, packagePath);
        } else {
            // Add only the needed module
            var optionalClass = importedSymbols.classes().stream()
                .filter(currentClass -> currentClass.name.equals(module))
                .findFirst();

            if (optionalClass.isPresent()) {
                data.classes().add(optionalClass.get());
                data.bindingContext().put(optionalClass.get(), importNode.module);
                return;
            }

            var optionalInterface = importedSymbols.interfaces().stream()
                .filter(currentInterface -> currentInterface.name.equals(module))
                .findFirst();

            if (optionalInterface.isPresent()) {
                data.interfaces().add(optionalInterface.get());
                data.bindingContext().put(optionalInterface.get(), importNode.module);
                return;
            }

            var optionalFunction = importedSymbols.functions().stream()
                .filter(currentFunction -> currentFunction.name.equals(module))
                .findFirst();

            if (optionalFunction.isPresent()) {
                data.functions().add(optionalFunction.get());
                data.bindingContext().put(optionalFunction.get(), importNode.module);
                return;
            }

            var optionalField = importedSymbols.fields().stream()
                .filter(currentField -> currentField.initialization.declaration.name.equals(module))
                .findFirst();

            if (optionalField.isPresent()) {
                data.fields().add(optionalField.get());
                data.bindingContext().put(optionalField.get(), importNode.module);
                return;
            }

            throw new SA_SemanticError("Symbol not found in package: " + module);
        }
    }
}
