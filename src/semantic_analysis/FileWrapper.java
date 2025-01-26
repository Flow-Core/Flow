package semantic_analysis;

import parser.nodes.components.BlockNode;

public record FileWrapper (
    BlockNode root,
    SymbolTable symbolTable
) { }
