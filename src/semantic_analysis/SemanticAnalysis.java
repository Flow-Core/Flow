package semantic_analysis;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.loaders.ClassLoader;
import semantic_analysis.loaders.*;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.scopes.TypeRecognize;
import semantic_analysis.transformers.TopLevelTransformer;
import semantic_analysis.visitors.ClassTraverse;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class SemanticAnalysis {
    final Map<String, PackageWrapper> packages;
    final Map<String, PackageWrapper> libs;

    public SemanticAnalysis(
        final Map<String, PackageWrapper> packages,
        final Map<String, PackageWrapper> libs
    ) {
        this.packages = packages;
        this.libs = libs;
    }

    public Map<String, PackageWrapper> analyze() {
        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                SignatureLoader.load(file, file.scope().symbols(), currentPackageWrapper);
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                final Map<String, PackageWrapper> packagesWithLibs = new HashMap<>();
                packagesWithLibs.putAll(packages);
                packagesWithLibs.putAll(libs);

                TypeRecognize.init(packagesWithLibs);

                new ImportLoader().load(file, file.scope().symbols(), packagesWithLibs);
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                file.root().accept(new ClassLoader(), file.scope());
            }
        }

        Map<TypeDeclarationNode, Scope> typeScopes = new IdentityHashMap<>();

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                for (final ASTNode node : file.root().children) {
                    if (node instanceof FunctionDeclarationNode function)
                        FunctionLoader.loadSignature(function, file.scope(), true);
                    else if (node instanceof ClassDeclarationNode classDeclaration) {
                        typeScopes.put(
                            classDeclaration,
                            new Scope(file.scope(), SymbolTable.getEmptySymbolTable(), classDeclaration, Scope.Type.CLASS)
                        );

                        ClassTraverse.loadMethodSignatures(classDeclaration, typeScopes.get(classDeclaration), false);
                    } else if (node instanceof InterfaceNode interfaceNode) {
                        typeScopes.put(
                            interfaceNode,
                            new Scope(file.scope(), SymbolTable.getEmptySymbolTable(), interfaceNode, Scope.Type.CLASS)
                        );

                        ClassTraverse.loadMethodSignatures(interfaceNode, typeScopes.get(interfaceNode), true);
                    }
                }
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                for (final ASTNode node : file.root().children) {
                    if (node instanceof FieldNode field) {
                        VariableLoader.loadDeclaration(field, file.scope());
                    } else if (node instanceof ClassDeclarationNode classDeclaration) {
                        ClassTraverse.loadFields(classDeclaration, typeScopes.get(classDeclaration));
                    }
                }
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                for (final ASTNode node : file.root().children) {
                    if (node instanceof FunctionDeclarationNode function)
                        FunctionLoader.loadBody(function, file.scope());
                    else if (node instanceof ClassDeclarationNode classDeclaration) {
                        ClassTraverse.loadMethodBodies(classDeclaration, typeScopes.get(classDeclaration));
                        ClassTraverse.loadConstructors(classDeclaration, typeScopes.get(classDeclaration));
                    }
                }
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                TopLevelTransformer.transform(file, currentPackageWrapper.path());
            }
        }

        return packages;
    }
}