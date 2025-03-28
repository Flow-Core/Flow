package parser.nodes.packages;

import parser.nodes.ASTNode;

import java.util.Objects;

public class ImportNode implements ASTNode {
    public String module;
    public String alias;
    public boolean isWildcard;

    public ImportNode(String module, String alias, boolean isWildcard) {
        this.module = module;
        this.alias = alias;
        this.isWildcard = isWildcard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportNode that = (ImportNode) o;

        if (isWildcard != that.isWildcard) return false;
        if (!Objects.equals(module, that.module)) return false;
        return Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (isWildcard ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImportNode{" +
            "module='" + module + '\'' +
            ", alias='" + alias + '\'' +
            ", isWildcard=" + isWildcard +
            '}';
    }
}
