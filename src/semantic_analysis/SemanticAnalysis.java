package semantic_analysis;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.classes.InterfaceNode;
import parser.nodes.classes.TypeDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.loaders.ClassLoader;
import semantic_analysis.loaders.ImportLoader;
import semantic_analysis.loaders.SignatureLoader;
import semantic_analysis.loaders.VariableLoader;
import semantic_analysis.scopes.Scope;
import semantic_analysis.visitors.ClassTraverse;
import semantic_analysis.loaders.FunctionLoader;

import java.util.HashMap;
import java.util.Map;

public class SemanticAnalysis {
    final Map<String, PackageWrapper> packages;

    public SemanticAnalysis(final Map<String, PackageWrapper> packages) {
        this.packages = packages;
    }

    public void analyze() {
        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                SignatureLoader.load(file.root().children, file.scope().symbols(), currentPackageWrapper);
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                new ImportLoader().load(file.root(), file.scope().symbols(), packages);
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                file.root().accept(new ClassLoader(currentPackageWrapper.scope().symbols()), file.scope().symbols());
            }
        }

        Map<TypeDeclarationNode, Scope> typeScopes = new HashMap<>();

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                for (final ASTNode node : file.root().children) {
                    if (node instanceof FunctionDeclarationNode function)
                        FunctionLoader.loadSignature(function, file.scope());
                }

                for (final ClassDeclarationNode classDeclaration : file.scope().symbols().classes()) {
                    typeScopes.put(
                        classDeclaration,
                        new Scope(file.scope(), SymbolTable.getEmptySymbolTable(), null, Scope.Type.CLASS)
                    );

                    ClassTraverse.loadMethodSignatures(classDeclaration, typeScopes.get(classDeclaration));
                }

                for (final InterfaceNode interfaceNode : file.scope().symbols().interfaces()) {
                    typeScopes.put(
                        interfaceNode,
                        new Scope(file.scope(), SymbolTable.getEmptySymbolTable(), null, Scope.Type.CLASS)
                    );

                    ClassTraverse.loadMethodSignatures(interfaceNode, typeScopes.get(interfaceNode));
                }
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                for (final ASTNode node : file.root().children) {
                    if (node instanceof FieldNode field) {
                        VariableLoader.loadDeclaration(field, file.scope());
                    }
                }

                for (final ClassDeclarationNode classDeclaration : file.scope().symbols().classes()) {
                    ClassTraverse.loadFields(classDeclaration, typeScopes.get(classDeclaration));
                }
            }
        }

        for (final PackageWrapper currentPackageWrapper : packages.values()) {
            for (final FileWrapper file : currentPackageWrapper.files()) {
                for (final ASTNode node : file.root().children) {
                    if (node instanceof FunctionDeclarationNode function)
                        FunctionLoader.loadBody(function, file.scope());
                }

                for (final ClassDeclarationNode classDeclaration : file.scope().symbols().classes()) {
                    ClassTraverse.loadMethodBodies(classDeclaration, typeScopes.get(classDeclaration));
                }
            }
        }
    }
}