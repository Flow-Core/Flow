package semantic_analysis;

import parser.nodes.components.BlockNode;

import java.util.List;

public record Package(
    String path,
    List<BlockNode> files,
    SymbolTable symbolTable
) { }