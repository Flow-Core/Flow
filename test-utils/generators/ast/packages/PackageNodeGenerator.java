package generators.ast.packages;

import parser.nodes.packages.PackageNode;

public class PackageNodeGenerator {
    private String packagePath = "main.test";

    public static PackageNodeGenerator builder() {
        return new PackageNodeGenerator();
    }

    public PackageNodeGenerator packagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public PackageNode build() {
        return new PackageNode(packagePath);
    }
}