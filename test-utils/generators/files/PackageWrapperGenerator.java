package generators.files;

import generators.scopes.ScopeGenerator;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;

import java.util.ArrayList;
import java.util.List;

public class PackageWrapperGenerator {
    private String path = "main.test";
    private List<FileWrapper> files = new ArrayList<>();
    private Scope scope = ScopeGenerator.builder().build();

    public static PackageWrapperGenerator builder() {
        return new PackageWrapperGenerator();
    }

    public PackageWrapperGenerator path(String path) {
        this.path = path;
        return this;
    }

    public PackageWrapperGenerator files(List<FileWrapper> files) {
        this.files = files;
        return this;
    }

    public PackageWrapperGenerator scope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public PackageWrapper build() {
        return new PackageWrapper(path, files, scope);
    }
}
