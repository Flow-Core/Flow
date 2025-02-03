package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;
import parser.nodes.packages.PackageNode;
import semantic_analysis.FileWrapper;
import semantic_analysis.PackageWrapper;
import semantic_analysis.SymbolTable;
import semantic_analysis.scopes.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageMapper {
    public static Map<String, PackageWrapper> map(final List<BlockNode> roots) {
        final Map<String, PackageWrapper> packages = new HashMap<>();

        for (BlockNode root : roots) {
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

            Scope fileScope = new Scope(packageWrapper.scope(), SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP);
            FileWrapper fileWrapper = new FileWrapper(root, fileScope);

            packageWrapper.files().add(fileWrapper);
        }

        return packages;
    }
}
