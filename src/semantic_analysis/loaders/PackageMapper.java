package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.components.BlockNode;
import parser.nodes.packages.PackageNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.SymbolTable;
import semantic_analysis.scopes.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageMapper {
    public static Map<String, PackageWrapper> map(
        final List<BlockNode> roots,
        final List<String> fileNames
    ) {
        if (roots.size() != fileNames.size()) {
            throw new IllegalArgumentException("Roots should be the same size as the file names");
        }

        final Map<String, PackageWrapper> packages = new HashMap<>();

        for (int i = 0; i < roots.size(); i++) {
            BlockNode root = roots.get(i);
            String fileName = fileNames.get(i);
            if (root.children.isEmpty()) {
                continue;
            }

            final ASTNode firstNode = root.children.get(0);
            String packagePath = "";
            if (firstNode instanceof PackageNode packageNode) {
                packagePath = packageNode.packagePath;
            }

            PackageWrapper packageWrapper = packages.computeIfAbsent(
                packagePath,
                path -> new PackageWrapper(path, new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP))
            );

            FileWrapper fileWrapper = getFileWrapper(packageWrapper, fileName, root);

            packageWrapper.files().add(fileWrapper);
        }

        return packages;
    }

    private static FileWrapper getFileWrapper(PackageWrapper packageWrapper, String fileName, BlockNode root) {
        Scope fileScope = new Scope(
            packageWrapper.scope(),
            SymbolTable.getEmptySymbolTable(),
            new ClassDeclarationNode(
                fileName + "Fl",
                List.of("public"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                new BlockNode(new ArrayList<>())
            ),
            Scope.Type.TOP
        );
        return new FileWrapper(root, fileScope, fileName);
    }
}