package parser.nodes.packages;

import parser.nodes.ASTNode;

public class PackageNode implements ASTNode {
    public String packagePath;

    public PackageNode(String packagePath) {
        this.packagePath = packagePath;
    }

    @Override
    public String toString() {
        return "PackageNode{" +
            "packagePath='" + packagePath + '\'' +
            '}';
    }
}
