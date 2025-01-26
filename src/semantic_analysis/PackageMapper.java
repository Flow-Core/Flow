package semantic_analysis;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;
import parser.nodes.packages.PackageNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageMapper {
    public static Map<String, Package> map(final List<BlockNode> roots) {
        final Map<String, Package> packages = new HashMap<>();

        for (BlockNode root : roots) {
            if (root.children.isEmpty()) {
                continue;
            }

            final ASTNode firstNode = root.children.get(0);
            String packagePath = "";
            if (firstNode instanceof PackageNode packageNode) {
                packagePath = packageNode.packagePath;
            }

            packages.computeIfAbsent(
                packagePath,
                path -> new Package(path, new ArrayList<>(), SymbolTable.getEmptySymbolTable())
            ).files().add(root);
        }

        return packages;
    }
}
