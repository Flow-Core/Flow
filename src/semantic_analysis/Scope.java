package semantic_analysis;

public record Scope (
    Scope parent,
    SymbolTable symbols
) {}