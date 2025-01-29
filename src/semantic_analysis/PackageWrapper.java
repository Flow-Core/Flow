package semantic_analysis;

import semantic_analysis.scopes.Scope;

import java.util.List;

public record PackageWrapper(
    String path,
    List<FileWrapper> files,
    Scope scope
) { }