package parser.nodes.packages;

import parser.nodes.ASTNode;

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
    public String toString() {
        return "ImportNode{" +
            "module='" + module + '\'' +
            ", alias='" + alias + '\'' +
            ", isWildcard=" + isWildcard +
            '}';
    }
}
