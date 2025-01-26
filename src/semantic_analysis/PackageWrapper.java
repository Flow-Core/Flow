package semantic_analysis;

import java.util.List;

public record PackageWrapper(
    String path,
    List<FileWrapper> files,
    SymbolTable symbolTable
) { }