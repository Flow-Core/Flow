package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;
import parser.nodes.packages.ImportNode;
import parser.nodes.packages.PackageNode;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.exceptions.SA_SemanticError;
import semantic_analysis.exceptions.SA_UnresolvedPackageException;

import java.util.Map;

public class ImportLoader {
    public void load(final BlockNode root, final SymbolTable data, final Map<String, PackageWrapper> globalPackages) {
        for (int i = 0; i < root.children.size(); i++) {
            final ASTNode node = root.children.get(i);
            if (node instanceof ImportNode importNode) {
                validateImport(importNode, data, globalPackages);
            } else if (i != 0 && node instanceof PackageNode) {
                throw new SA_SemanticError("Package must be on top of the file");
            }
        }
    }

    private void validateImport(final ImportNode importNode, final SymbolTable data, final Map<String, PackageWrapper> globalPackages) {
        final String modulePath = importNode.module;

        final int lastDotIndex = modulePath.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new SA_SemanticError("No module included in import (use wildcard: '*' to import all)");
        }

        final String packagePath = modulePath.substring(0, lastDotIndex);
        final String module = modulePath.substring(lastDotIndex + 1);

        if (!globalPackages.containsKey(packagePath)) {
            LoggerFacade.getLogger().log(
                Logger.Severity.ERROR,
                "Package not found: '" + packagePath + "'",
                ASTMetaDataStore.getInstance().getLine(importNode),
                ASTMetaDataStore.getInstance().getFile(importNode)
            );
            return;
        }

        final SymbolTable importedSymbols = globalPackages.get(packagePath).scope().symbols();
        if (importNode.isWildcard) {
            if (!importNode.alias.equals("*")) {
                LoggerFacade.getLogger().log(
                    Logger.Severity.ERROR,
                    "Cannot rename all imported items to one identifier",
                    ASTMetaDataStore.getInstance().getLine(importNode),
                    ASTMetaDataStore.getInstance().getFile(importNode)
                );
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

            LoggerFacade.getLogger().log(
                Logger.Severity.ERROR,
                "Symbol not found in package: " + module,
                ASTMetaDataStore.getInstance().getLine(importNode),
                ASTMetaDataStore.getInstance().getFile(importNode)
            );
        }
    }
}
