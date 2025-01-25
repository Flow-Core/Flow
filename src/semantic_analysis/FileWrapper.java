package semantic_analysis;

import parser.nodes.components.BlockNode;

public record FileWrapper(
    String path,
    BlockNode root,
    SymbolTable symbolTable
) { }