package semantic_analysis.loaders;

import logger.LoggerFacade;
import parser.nodes.ASTMetaDataStore;
import parser.nodes.ASTNode;
import parser.nodes.classes.BaseInterfaceNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.packages.ImportNode;
import parser.nodes.packages.PackageNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.scopes.TypeRecognize;

import java.util.Map;

public class ImportLoader {
    public void load(final FileWrapper file, final SymbolTable data, final Map<String, PackageWrapper> globalPackages) {
        boolean finishedImports = false;

        validateImport(
            (ImportNode) ASTMetaDataStore.getInstance().addMetadata(
                new ImportNode(
                    "flow.*",
                    "*",
                    true
                ),
                0,
                file.name()
            ),
            data,
            globalPackages
        );

        validateImport(
            (ImportNode) ASTMetaDataStore.getInstance().addMetadata(
                new ImportNode(
                    "java.lang.Object",
                    "Object",
                    false
                ),
                0,
                file.name()
            ),
            data,
            globalPackages
        );

        for (int i = 0; i < file.root().children.size(); i++) {
            final ASTNode node = file.root().children.get(i);
            if (node instanceof ImportNode importNode) {
                if (finishedImports) {
                    LoggerFacade.error("Import cannot be here", node);
                }

                validateImport(importNode, data, globalPackages);
            } else if (node instanceof PackageNode) {
                if (i != 0) {
                    LoggerFacade.error("Package must be on top of the file", node);
                }
            } else {
                finishedImports = true;
            }
        }
    }

    private void validateImport(final ImportNode importNode, final SymbolTable data, final Map<String, PackageWrapper> globalPackages) {
        final String modulePath = importNode.module;

        final int lastDotIndex = modulePath.lastIndexOf(".");
        if (lastDotIndex == -1) {
            LoggerFacade.error("No module included in import (use wildcard: '*' to import all)", importNode);
        }

        final String packagePath = modulePath.substring(0, lastDotIndex);
        final String module = modulePath.substring(lastDotIndex + 1);

        if (!globalPackages.containsKey(packagePath)) {
            LoggerFacade.error("Package not found: '" + packagePath + "'", importNode);
            return;
        }

        final SymbolTable importedSymbols = globalPackages.get(packagePath).scope().symbols();
        if (importNode.isWildcard) {
            if (!importNode.alias.equals("*")) {
                LoggerFacade.error("Cannot rename all imported items to one identifier" + packagePath + "'", importNode);
            }

            data.recognizeSymbolTable(importedSymbols);
            data.addToBindingContext(importedSymbols);
        } else {
            // Add only the needed module
            var optionalClass = importedSymbols.classes().stream()
                .filter(currentClass -> currentClass.name.equals(module))
                .findFirst();

            Scope scope = new Scope(
                new Scope(
                    null,
                    importedSymbols,
                    null,
                    Scope.Type.TOP
                ),
                data,
                null,
                Scope.Type.TOP
            );

            if (optionalClass.isPresent()) {
                data.classes().add(optionalClass.get());
                data.bindingContext().put(optionalClass.get(), importNode.module);

                addBaseTypesRecursively(optionalClass.get(), scope, importedSymbols);

                return;
            }

            var optionalInterface = importedSymbols.interfaces().stream()
                .filter(currentInterface -> currentInterface.name.equals(module))
                .findFirst();

            if (optionalInterface.isPresent()) {
                data.interfaces().add(optionalInterface.get());
                data.bindingContext().put(optionalInterface.get(), importNode.module);

                addBaseInterfacesRecursively(optionalInterface.get(), scope, importedSymbols);

                return;
            }

            var optionalFunction = importedSymbols.functions().stream()
                .filter(currentFunction -> currentFunction.name.equals(module))
                .findFirst();

            if (optionalFunction.isPresent()) {
                data.functions().add(optionalFunction.get());
                data.bindingContext().put(optionalFunction.get(), data.bindingContext().get(optionalFunction.get()));
                return;
            }

            var optionalField = importedSymbols.fields().stream()
                .filter(currentField -> currentField.initialization.declaration.name.equals(module))
                .findFirst();

            if (optionalField.isPresent()) {
                data.fields().add(optionalField.get());
                data.bindingContext().put(optionalField.get(), data.bindingContext().get(optionalField.get()));
                return;
            }

            LoggerFacade.error("Symbol not found in package: " + module, importNode);
        }
    }

    private void addBaseTypesRecursively(ClassDeclarationNode classDecl, Scope scope, SymbolTable importedSymbols) {
        if (!classDecl.baseClasses.isEmpty()) {
            ClassDeclarationNode baseDecl = TypeRecognize.getClass(classDecl.baseClasses.get(0).type.name, scope);
            if (baseDecl != null && !scope.symbols().bindingContext().containsKey(baseDecl)) {
                scope.symbols().bindingContext().put(baseDecl, importedSymbols.bindingContext().get(baseDecl));
                addBaseTypesRecursively(baseDecl, scope, importedSymbols);
            }
        }

        for (BaseInterfaceNode baseInterface : classDecl.implementedInterfaces) {
            InterfaceNode ifaceDecl = TypeRecognize.getInterface(baseInterface.type.name, scope);
            if (ifaceDecl != null && !scope.symbols().bindingContext().containsKey(baseInterface)) {
                scope.symbols().bindingContext().put(baseInterface, importedSymbols.bindingContext().get(ifaceDecl));
                addBaseInterfacesRecursively(ifaceDecl, scope, importedSymbols);
            }
        }
    }

    private void addBaseInterfacesRecursively(InterfaceNode iface, Scope scope, SymbolTable importedSymbols) {
        for (BaseInterfaceNode baseInterface : iface.implementedInterfaces) {
            InterfaceNode superIface = TypeRecognize.getInterface(baseInterface.type.name, scope);
            if (superIface != null && !scope.symbols().bindingContext().containsKey(baseInterface)) {
                scope.symbols().bindingContext().put(baseInterface, importedSymbols.bindingContext().get(superIface));
                addBaseInterfacesRecursively(superIface, scope, importedSymbols);
            }
        }
    }
}