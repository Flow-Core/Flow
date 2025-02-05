package generators.files;

import generators.scopes.ScopeGenerator;
import parser.nodes.components.BlockNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.scopes.Scope;

public class FileWrapperGenerator {
    private BlockNode root;
    private Scope scope = ScopeGenerator.builder().build();

    public static FileWrapperGenerator builder() {
        return new FileWrapperGenerator();
    }

    public FileWrapperGenerator root(BlockNode root) {
        this.root = root;
        return this;
    }

    public FileWrapperGenerator scope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public FileWrapper build() {
        return new FileWrapper(root, scope);
    }
}