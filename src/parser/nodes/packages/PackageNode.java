package parser.nodes.packages;

import parser.nodes.ASTNode;

import java.util.Objects;

public class PackageNode implements ASTNode {
    public String packagePath;

    public PackageNode(String packagePath) {
        this.packagePath = packagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageNode that = (PackageNode) o;

        return Objects.equals(packagePath, that.packagePath);
    }

    @Override
    public int hashCode() {
        return packagePath != null ? packagePath.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PackageNode{" +
            "packagePath='" + packagePath + '\'' +
            '}';
    }
}
