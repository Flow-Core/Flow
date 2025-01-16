package parser.nodes.packages;

import parser.nodes.ASTNode;

public class ImportNode implements ASTNode {
    public String modulePath;
    public String module;
    public String alias;

    public ImportNode(String modulePath, String module, String alias) {
        this.modulePath = modulePath;
        this.module = module;
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "ImportNode{" +
            "modulePath='" + modulePath + '\'' +
            ", module='" + module + '\'' +
            ", alias='" + alias + '\'' +
            '}';
    }
}
