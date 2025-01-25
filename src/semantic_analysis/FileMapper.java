package semantic_analysis;

import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;
import parser.nodes.packages.PackageNode;

import java.util.ArrayList;
import java.util.List;

public class FileMapper {
    public static List<FileWrapper> map(final List<BlockNode> roots) {
        final List<FileWrapper> files = new ArrayList<>();

        for (BlockNode root : roots) {
            if (root.children.isEmpty()) {
                continue;
            }

            final ASTNode firstNode = root.children.get(0);
            String packagePath = "";
            if (firstNode instanceof PackageNode packageNode) {
                packagePath = packageNode.packagePath;
            }
            files.add(new FileWrapper(packagePath, root, SymbolTable.getEmptySymbolTable()));
        }

        return files;
    }
}
