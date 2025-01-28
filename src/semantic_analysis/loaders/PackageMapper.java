package semantic_analysis.loaders;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;
import parser.nodes.packages.PackageNode;
import semantic_analysis.FileWrapper;
import semantic_analysis.PackageWrapper;
import semantic_analysis.SymbolTable;

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

            FileWrapper fileWrapper = new FileWrapper(root, SymbolTable.getEmptySymbolTable());
            packages.computeIfAbsent(
                packagePath,
                path -> new PackageWrapper(path, new ArrayList<>(), SymbolTable.getEmptySymbolTable())
            ).files().add(fileWrapper);
        }

        return packages;
    }
}
