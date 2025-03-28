package generators.ast.packages;

import parser.nodes.packages.ImportNode;

public class ImportNodeGenerator {
    private String module = "main.test.A";
    private String alias = null;
    private boolean isWildcard = false;

    public static ImportNodeGenerator builder() {
        return new ImportNodeGenerator();
    }

    public ImportNodeGenerator module(String module) {
        this.module = module;
        return this;
    }

    public ImportNodeGenerator alias(String alias) {
        this.alias = alias;
        return this;
    }

    public ImportNodeGenerator wildcard(boolean isWildcard) {
        this.isWildcard = isWildcard;
        return this;
    }

    public ImportNode build() {
        return new ImportNode(module, alias, isWildcard);
    }
}